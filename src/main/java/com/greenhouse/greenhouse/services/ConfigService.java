
package com.greenhouse.greenhouse.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenhouse.greenhouse.dtos.telemetry.DeviceConfigDTO;
import com.greenhouse.greenhouse.dtos.telemetry.MappingDTO;
import com.greenhouse.greenhouse.models.Greenhouse;
import com.greenhouse.greenhouse.repositories.GreenhouseRepository;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class ConfigService {

    private final GreenhouseRepository greenhouseRepository;
    private final MqttPublisher mqttPublisher;
    private final ObjectMapper objectMapper;

    public ConfigService(GreenhouseRepository greenhouseRepository, MqttPublisher mqttPublisher,
                         ObjectMapper objectMapper) {
        this.greenhouseRepository = greenhouseRepository;
        this.mqttPublisher = mqttPublisher;
        this.objectMapper = objectMapper;
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
        greenhouseRepository.save(gh);
        push(gh, "set/config", json);
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
        greenhouseRepository.save(gh);
        push(gh, "set/mapping", json);
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