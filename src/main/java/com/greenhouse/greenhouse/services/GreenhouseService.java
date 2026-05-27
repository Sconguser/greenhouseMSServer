package com.greenhouse.greenhouse.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenhouse.greenhouse.dtos.telemetry.*;
import com.greenhouse.greenhouse.exceptions.GreenhouseNotFoundException;
import com.greenhouse.greenhouse.mappers.GreenhouseMapper;
import com.greenhouse.greenhouse.mappers.ParameterMapper;
import com.greenhouse.greenhouse.mappers.ZoneMapper;
import com.greenhouse.greenhouse.models.Greenhouse;
import com.greenhouse.greenhouse.models.ParameterEntity;
import com.greenhouse.greenhouse.models.Status;
import com.greenhouse.greenhouse.models.Zone;
import com.greenhouse.greenhouse.repositories.GreenhouseRepository;
import com.greenhouse.greenhouse.repositories.ZoneRepository;
import com.greenhouse.greenhouse.requests.GreenhouseRequest;
import com.greenhouse.greenhouse.requests.ZoneRequest;
import com.greenhouse.greenhouse.responses.GreenhouseResponse;
import com.greenhouse.greenhouse.responses.ZoneResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GreenhouseService {
    private final GreenhouseRepository greenhouseRepository;
    private final GreenhouseMapper greenhouseMapper;
    private final ZoneMapper zoneMapper;
    private final ZoneRepository zoneRepository;
    private final ParameterMapper parameterMapper;
    private final MqttPublisher mqttService;
    private final ObjectMapper objectMapper;
    private final ConfigService configService;
    private final AnalyticsService analyticsService;

    @Autowired
    public GreenhouseService (GreenhouseRepository greenhouseRepository, GreenhouseMapper greenhouseMapper,
                              ZoneMapper zoneMapper, ZoneRepository zoneRepository, ParameterMapper parameterMapper,
                              MqttPublisher mqttPublisher, ObjectMapper objectMapper,
                              @org.springframework.context.annotation.Lazy ConfigService configService,
                              AnalyticsService analyticsService)
    {
        this.greenhouseRepository = greenhouseRepository;
        this.greenhouseMapper = greenhouseMapper;
        this.zoneMapper = zoneMapper;
        this.zoneRepository = zoneRepository;
        this.parameterMapper = parameterMapper;
        this.mqttService = mqttPublisher;
        this.objectMapper = objectMapper;
        this.configService = configService;
        this.analyticsService = analyticsService;
    }

    public GreenhouseResponse getGreenhouse (Long id) {
        return greenhouseMapper.toResponse(getGreenhouseEntity(id));
    }

    public List<GreenhouseResponse> getAllGreenhouses () {
        return greenhouseRepository.findAll()
                .stream()
                .map(greenhouseMapper::toResponse)
                .toList();
    }

    public Greenhouse getGreenhouseEntity (Long id) {
        return greenhouseRepository.findById(id)
                .orElseThrow(() -> new GreenhouseNotFoundException(
                        "Requested greenhouse with ID = " + id + "was not found"));
    }

    public GreenhouseResponse addGreenhouse(GreenhouseRequest request){
        Greenhouse greenhouse = greenhouseMapper.toEntity(request);
        Zone zone = new Zone();
        zone.setGreenhouse(greenhouse);
        zone.setName("Zone 1");
        greenhouse.addZone(zone);
        for (ParameterEntity parameterEntity : greenhouse.getParameters()) {
            parameterEntity.setGreenhouse(greenhouse);
        }
        Greenhouse greenhouseRepository = this.greenhouseRepository.save(greenhouse);
        return greenhouseMapper.toResponse(greenhouseRepository);
    }

    public GreenhouseResponse updateGreenhouse (GreenhouseRequest request, Long id) {
        Greenhouse greenhouse = getGreenhouseEntity(id);
        if (request.getName() != null) {
            greenhouse.setName(request.getName());
        }
        if (request.getIpAddress() != null) {
            greenhouse.setIpAddress(request.getIpAddress());
        }
        if (request.getLocation() != null) {
            greenhouse.setLocation(request.getLocation());
        }
        updateGreenhouse(greenhouse);
        return greenhouseMapper.toResponse(greenhouse);
    }

    public void updateGreenhouse(Greenhouse greenhouse){
        greenhouseRepository.save(greenhouse);
    }

    public void deleteGreenhouse (Long id) {
        greenhouseRepository.deleteById(id);
    }

    public ZoneResponse addZone (Long id, ZoneRequest zoneRequest) {
        Greenhouse greenhouse = getGreenhouseEntity(id);
        Zone zone = zoneMapper.toEntity(zoneRequest);
//        greenhouse.addZone(zone);
        zone.setGreenhouse(greenhouse);
//        greenhouseRepository.save(greenhouse);
        zoneRepository.save(zone);
        if (zone.getParameters() != null) {
            for (ParameterEntity parameterEntity : zone.getParameters()) {
                parameterEntity.setZone(zone);
            }
        }
        return zoneMapper.toResponse(zone);
    }

    public void removeZone (Long greenhouseId, Long zoneId) {
        Greenhouse greenhouse = getGreenhouseEntity(greenhouseId);
        greenhouse.removeZone(zoneId);
        greenhouseRepository.save(greenhouse);
    }

    @Transactional
    public void sendGreenhouseDataToGreenhouse(Long greenhouseId) {
        Greenhouse greenhouse = getGreenhouseEntity(greenhouseId);
        greenhouse.setLastPushed(LocalDateTime.now());
        greenhouse.setModelSynced(false);
        greenhouse.setModelDirtyAt(null);
        greenhouseRepository.save(greenhouse);
        pushModelToDevice(greenhouse);
    }

    @Transactional
    public void markModelSynced(String ipAddress) {
        greenhouseRepository.findByIpAddress(ipAddress).ifPresent(gh -> {
            gh.setModelSynced(true);
            greenhouseRepository.save(gh);
            System.out.printf("[MODEL] Model ACK received for %s%n", ipAddress);
        });
    }

    public void pushModelToDevice(Greenhouse greenhouse) {
        if (greenhouse.getIpAddress() == null || greenhouse.getIpAddress().isBlank()) return;
        try {
            String jsonPayload = objectMapper.writeValueAsString(greenhouse);
            String topic = "greenhouse/" + greenhouse.getIpAddress() + "/set/model";
            mqttService.sendCommand(topic, jsonPayload);
        } catch (Exception e) {
            System.err.println("Failed to push model to device: " + e.getMessage());
        }
    }

    @Transactional
    public void updateTelemetry(TelemetryGreenhouseDTO telemetry) {
        // 1. Find the Greenhouse
        Greenhouse gh = greenhouseRepository.findById(telemetry.id).orElse(null);
        if (gh == null) return; // Unknown greenhouse, ignore
        gh.setLastUpdate(LocalDateTime.now());

        // 2. Since we received data, the device is definitely ON.
        // Track whether it was previously offline so we can push the latest model after saving.
        boolean wasOffline = gh.getStatus() == Status.NOT_RESPONSIVE || gh.getStatus() == Status.OFF;
        if (wasOffline) {
            gh.setStatus(Status.ON);
        }
        // 2. Update Zones
        if (telemetry.zones != null) {
            for (TelemetryZoneDTO zDto : telemetry.zones) {
                // Find matching zone in the entity list
                gh.getZones().stream()
                        .filter(z -> z.getId().equals(zDto.id))
                        .findFirst()
                        .ifPresent(zone -> {
                            updateParams(zone.getParameters(), zDto.parameters);

                            // Update Flowerpots
                            if (zDto.flowerpots != null) {
                                for (TelemetryFlowerpotDTO fpDto : zDto.flowerpots) {
                                    zone.getFlowerpots().stream()
                                            .filter(fp -> fp.getId().equals(fpDto.id))
                                            .findFirst()
                                            .ifPresent(fp -> updateParams(fp.getParameters(), fpDto.parameters));
                                }
                            }
                        });
            }
        }

        // 3. Save Changes
        greenhouseRepository.save(gh);

        // 4. Record parameter history for analytics (every telemetry tick).
        analyticsService.recordTelemetryReadings(gh, telemetry);

        // 5. If the device just came back online: record a BOOT event and
        //    start the sequential push sequence:
        //    config → (ACK) → mapping → (ACK) → model.
        //    Sending all three at once would cause multiple reboots before any file is saved.
        if (wasOffline) {
            analyticsService.recordBoot(gh.getId());
            gh.setModelSynced(false);
            greenhouseRepository.save(gh);
            try {
                if (Boolean.FALSE.equals(gh.getDeviceConfigSynced())) {
                    configService.pushDeviceConfig(gh);
                } else if (Boolean.FALSE.equals(gh.getMappingConfigSynced())) {
                    configService.pushMappingConfig(gh);
                } else {
                    pushModelToDevice(gh);
                }
            } catch (Exception e) {
                System.err.println("Failed to start reconnect push sequence: " + e.getMessage());
            }
        }
    }

    // Helper to update a list of parameters
    private void updateParams(List<ParameterEntity> entities, List<TelemetryParameterDTO> dtos) {
        if (dtos == null || entities == null) return;

        for (TelemetryParameterDTO pDto : dtos) {
            entities.stream()
                    .filter(p -> p.getId().equals(pDto.id))
                    .findFirst()
                    .ifPresent(p -> {
                        if (pDto.val != null) {
                            p.setCurrentValue(pDto.val);
                        }
                    });
        }
    }
    public void sendConfiguration(Long greenhouseId, List<DeviceConfigDTO> configList) {
        Greenhouse gh = greenhouseRepository.findById(greenhouseId).orElse(null);
        if (gh == null) return;

        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonPayload = mapper.writeValueAsString(configList);

            // Topic: greenhouse/{IP}/set/config
            String topic = "greenhouse/" + gh.getIpAddress() + "/set/config";

            mqttService.sendCommand(topic, jsonPayload);
            System.out.println("Sent CONFIG to " + topic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMapping(Long greenhouseId, List<MappingDTO> mappingList) {
        Greenhouse gh = greenhouseRepository.findById(greenhouseId).orElse(null);
        if (gh == null) return;

        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonPayload = mapper.writeValueAsString(mappingList);

            // Topic: greenhouse/{IP}/set/mapping
            String topic = "greenhouse/" + gh.getIpAddress() + "/set/mapping";

            mqttService.sendCommand(topic, jsonPayload);
            System.out.println("Sent MAPPING to " + topic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
