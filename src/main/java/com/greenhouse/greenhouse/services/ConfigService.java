
package com.greenhouse.greenhouse.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenhouse.greenhouse.dtos.telemetry.DeviceConfigDTO;
import com.greenhouse.greenhouse.dtos.telemetry.MappingDTO;
import com.greenhouse.greenhouse.models.Greenhouse;
import com.greenhouse.greenhouse.repositories.GreenhouseRepository;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class ConfigService {

    private final GreenhouseRepository greenhouseRepository;
    private final MqttPublisher mqttPublisher;
    private final ObjectMapper objectMapper;
    private final GreenhouseService greenhouseService;

    public ConfigService(GreenhouseRepository greenhouseRepository, MqttPublisher mqttPublisher,
                         ObjectMapper objectMapper,
                         @Lazy GreenhouseService greenhouseService) {
        this.greenhouseRepository = greenhouseRepository;
        this.mqttPublisher = mqttPublisher;
        this.objectMapper = objectMapper;
        this.greenhouseService = greenhouseService;
    }

    // -------------------------------------------------------------------------
    // Device config  (physical sensor/actuator hardware list)
    // Arduino topic:  greenhouse/{IP}/set/config
    // Arduino file:   /config.json
    // Arduino action: saves JSON and reboots after 2 seconds.
    // -------------------------------------------------------------------------

    public List<DeviceConfigDTO> getDeviceConfig(Long greenhouseId) throws Exception {
        Greenhouse gh = require(greenhouseId);
        if (gh.getDeviceConfigJson() == null) return Collections.emptyList();
        return objectMapper.readValue(gh.getDeviceConfigJson(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, DeviceConfigDTO.class));
    }

    @Transactional
    public void saveAndPushDeviceConfig(Long greenhouseId, List<DeviceConfigDTO> dtos) throws Exception {
        Greenhouse gh = require(greenhouseId);
        String json = objectMapper.writeValueAsString(dtos);
        gh.setDeviceConfigJson(json);
        gh.setDeviceConfigSynced(false);
        greenhouseRepository.save(gh);
        try {
            pushDeviceConfig(gh);
        } catch (Exception e) {
            System.err.println("[CONFIG] Device config saved to DB but MQTT push failed (will retry on reconnect): " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Mapping config  (sensor-to-parameter bindings + control logic)
    // Arduino topic:  greenhouse/{IP}/set/mapping
    // Arduino file:   /mapping.json
    // Arduino action: saves JSON and reboots after 2 seconds.
    // -------------------------------------------------------------------------

    public List<MappingDTO> getMappingConfig(Long greenhouseId) throws Exception {
        Greenhouse gh = require(greenhouseId);
        if (gh.getMappingConfigJson() == null) return Collections.emptyList();
        return objectMapper.readValue(gh.getMappingConfigJson(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, MappingDTO.class));
    }

    @Transactional
    public void saveAndPushMappingConfig(Long greenhouseId, List<MappingDTO> dtos) throws Exception {
        Greenhouse gh = require(greenhouseId);
        String json = objectMapper.writeValueAsString(dtos);
        gh.setMappingConfigJson(json);
        gh.setMappingConfigSynced(false);
        greenhouseRepository.save(gh);
        try {
            pushMappingConfig(gh);
        } catch (Exception e) {
            System.err.println("[CONFIG] Mapping config saved to DB but MQTT push failed (will retry on reconnect): " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Push-only methods — used on reconnect re-push without touching DB state
    // -------------------------------------------------------------------------

    public void pushDeviceConfig(Greenhouse gh) throws MqttException {
        if (gh.getDeviceConfigJson() == null) return;
        push(gh, "set/config", gh.getDeviceConfigJson());
    }

    public void pushMappingConfig(Greenhouse gh) throws MqttException {
        if (gh.getMappingConfigJson() == null) return;
        push(gh, "set/mapping", gh.getMappingConfigJson());
    }

    // -------------------------------------------------------------------------
    // ACK handlers — called when board confirms it saved the file.
    // Chain: if the next item in the sequence is still pending, push it now.
    // -------------------------------------------------------------------------

    @Transactional
    public void markDeviceConfigSynced(String ipAddress) {
        greenhouseRepository.findByIpAddress(ipAddress).ifPresent(gh -> {
            gh.setDeviceConfigSynced(true);
            greenhouseRepository.save(gh);
            System.out.printf("[CONFIG] Device config ACK received for %s%n", ipAddress);
            try {
                if (Boolean.FALSE.equals(gh.getMappingConfigSynced())) {
                    pushMappingConfig(gh);
                } else if (Boolean.FALSE.equals(gh.getModelSynced())) {
                    greenhouseService.pushModelToDevice(gh);
                }
            } catch (Exception e) {
                System.err.println("[CONFIG] Sequential push after config ACK failed: " + e.getMessage());
            }
        });
    }

    @Transactional
    public void markMappingConfigSynced(String ipAddress) {
        greenhouseRepository.findByIpAddress(ipAddress).ifPresent(gh -> {
            gh.setMappingConfigSynced(true);
            greenhouseRepository.save(gh);
            System.out.printf("[CONFIG] Mapping config ACK received for %s%n", ipAddress);
            try {
                if (Boolean.FALSE.equals(gh.getModelSynced())) {
                    greenhouseService.pushModelToDevice(gh);
                }
            } catch (Exception e) {
                System.err.println("[CONFIG] Sequential push after mapping ACK failed: " + e.getMessage());
            }
        });
    }

    // -------------------------------------------------------------------------

    private void push(Greenhouse gh, String subtopic, String json) throws MqttException {
        if (gh.getIpAddress() == null || gh.getIpAddress().isBlank()) return;
        mqttPublisher.sendCommand("greenhouse/" + gh.getIpAddress() + "/" + subtopic, json);
    }

    private Greenhouse require(Long id) {
        return greenhouseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Greenhouse not found: " + id));
    }
}
