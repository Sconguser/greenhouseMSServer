package com.greenhouse.greenhouse.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenhouse.greenhouse.dtos.telemetry.*;
import com.greenhouse.greenhouse.exceptions.GreenhouseNotFoundException;
import com.greenhouse.greenhouse.mappers.GreenhouseMapper;
import com.greenhouse.greenhouse.mappers.ParameterMapper;
import com.greenhouse.greenhouse.mappers.ZoneMapper;
import com.greenhouse.greenhouse.models.Greenhouse;
import com.greenhouse.greenhouse.models.ParameterEntity;
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

import java.util.List;

@Service
public class GreenhouseService {
    private final GreenhouseRepository greenhouseRepository;
    private final GreenhouseMapper greenhouseMapper;
    private final ZoneMapper zoneMapper;
    private final ZoneRepository zoneRepository;
    private final ParameterMapper parameterMapper;
    private final MqttPublisher mqttService;

    @Autowired
    public GreenhouseService (GreenhouseRepository greenhouseRepository, GreenhouseMapper greenhouseMapper,
                              ZoneMapper zoneMapper, ZoneRepository zoneRepository, ParameterMapper parameterMapper, MqttPublisher mqttPublisher)
    {
        this.greenhouseRepository = greenhouseRepository;
        this.greenhouseMapper = greenhouseMapper;
        this.zoneMapper = zoneMapper;
        this.zoneRepository = zoneRepository;
        this.parameterMapper = parameterMapper;
        this.mqttService = mqttPublisher;
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

    public void sendGreenhouseDataToGreenhouse(Long greenhouseId){
        Greenhouse greenhouse = getGreenhouseEntity(greenhouseId);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonPayload = objectMapper.writeValueAsString(greenhouse);
            mqttService.sendCommand(greenhouse.getIpAddress(), jsonPayload);
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    @Transactional
    public void updateTelemetry(TelemetryGreenhouseDTO telemetry) {
        // 1. Find the Greenhouse
        Greenhouse gh = greenhouseRepository.findById(telemetry.id).orElse(null);
        if (gh == null) return; // Unknown greenhouse, ignore

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
