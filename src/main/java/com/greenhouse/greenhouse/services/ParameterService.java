package com.greenhouse.greenhouse.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenhouse.greenhouse.dtos.ParameterDTO;
import com.greenhouse.greenhouse.exceptions.FlowerpotNotFoundException;
import com.greenhouse.greenhouse.exceptions.GreenhouseNotFoundException;
import com.greenhouse.greenhouse.exceptions.ZoneNotFoundException;
import com.greenhouse.greenhouse.mappers.ParameterMapper;
import com.greenhouse.greenhouse.models.Flowerpot;
import com.greenhouse.greenhouse.models.Greenhouse;
import com.greenhouse.greenhouse.models.ParameterEntity;
import com.greenhouse.greenhouse.models.Zone;
import com.greenhouse.greenhouse.repositories.FlowerpotRepository;
import com.greenhouse.greenhouse.repositories.GreenhouseRepository;
import com.greenhouse.greenhouse.repositories.ParameterRepository;
import com.greenhouse.greenhouse.repositories.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ParameterService {
    @Autowired
    private ParameterMapper parameterMapper;
    @Autowired
    private ParameterRepository parameterRepository;
    @Autowired
    private GreenhouseRepository greenhouseRepository;
    @Autowired
    private ZoneRepository zoneRepository;
    @Autowired
    private FlowerpotRepository flowerpotRepository;
    @Autowired
    private MqttService mqttService;
    @Autowired
    private GreenhouseService greenhouseService;

    public ParameterDTO addToGreenhouse (Long greenhouseId, ParameterDTO parameterDTO) {
        Greenhouse greenhouse = greenhouseRepository.findById(greenhouseId)
                .orElseThrow(
                        () -> new GreenhouseNotFoundException("Greenhouse with id " + greenhouseId + " was not found"));
        if (parameterAlreadyExists(greenhouse.getParameters(), parameterDTO)) {
            throw new IllegalArgumentException(
                    "Parameter with name " + parameterDTO.getName() + " already exists in greenhouse " + greenhouseId);
        }
        ParameterEntity entity = parameterMapper.toEntity(parameterDTO);
        entity.setGreenhouse(greenhouse);
        entity.setZone(null);
        entity.setFlowerpot(null);
        ParameterEntity saved = parameterRepository.save(entity);
        return parameterMapper.toDto(saved);
    }

    public ParameterDTO addToZone (Long zoneId, ParameterDTO parameterDTO) {
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ZoneNotFoundException("Zone with id " + zoneId + " was not found"));
        if (parameterAlreadyExists(zone.getParameters(), parameterDTO)) {
            throw new IllegalArgumentException(
                    "Parameter with name " + parameterDTO.getName() + " already exists in zone " + zoneId);
        }
        ParameterEntity entity = parameterMapper.toEntity(parameterDTO);
        entity.setGreenhouse(null);
        entity.setZone(zone);
        entity.setFlowerpot(null);
        ParameterEntity saved = parameterRepository.save(entity);
        return parameterMapper.toDto(saved);
    }

    public ParameterDTO addToFlowerpot (Long flowerpotId, ParameterDTO parameterDTO) {
        Flowerpot flowerpot = flowerpotRepository.findById(flowerpotId)
                .orElseThrow(
                        () -> new FlowerpotNotFoundException("Flowerpoty with id " + flowerpotId + " was not found"));
        if (parameterAlreadyExists(flowerpot.getParameters(), parameterDTO)) {
            throw new IllegalArgumentException(
                    "Parameter with name " + parameterDTO.getName() + " already exists in flowerpot " + flowerpotId);
        }
        ParameterEntity entity = parameterMapper.toEntity(parameterDTO);
        entity.setGreenhouse(null);
        entity.setZone(null);
        entity.setFlowerpot(flowerpot);
        ParameterEntity saved = parameterRepository.save(entity);
        return parameterMapper.toDto(saved);
    }

    private boolean parameterAlreadyExists (List<ParameterEntity> parameterEntities, ParameterDTO parameterDTO) {
        return parameterEntities.stream()
                .anyMatch(entity -> entity.getName()
                        .equals(parameterDTO.getName()));
    }

    public List<ParameterDTO> getGreenhouseParameters (Long greenhouseId) {
        return parameterRepository.findByGreenhouseId(greenhouseId)
                .stream()
                .map(parameterMapper::toDto)
                .toList();
    }

    public List<ParameterDTO> getZoneParameters (Long zoneId) {
        return parameterRepository.findByZoneId(zoneId)
                .stream()
                .map(parameterMapper::toDto)
                .toList();
    }

    public List<ParameterDTO> getFlowerpotParameters (Long flowerpotId) {
        return parameterRepository.findByFlowerpotId(flowerpotId)
                .stream()
                .map(parameterMapper::toDto)
                .toList();
    }

    private Long findGreenhouseId (List<ParameterEntity> parameterEntities) {
        if (parameterEntities.get(0)
                .getGreenhouse() != null)
        {
            return parameterEntities.get(0)
                    .getGreenhouse()
                    .getId();
        }
        if (parameterEntities.get(0)
                .getZone() != null)
        {
            return parameterEntities.get(0)
                    .getZone()
                    .getGreenhouse()
                    .getId();
        }
        if (parameterEntities.get(0)
                .getFlowerpot() != null)
        {
            return parameterEntities.get(0)
                    .getFlowerpot()
                    .getZone()
                    .getGreenhouse()
                    .getId();
        }
        return null;
    }

    public void deleteParameter (Long parameterId) {
        parameterRepository.deleteById(parameterId);
    }

    @Transactional
    public List<ParameterDTO> updateParameters (List<ParameterDTO> parameterDTOS) {
        if (parameterDTOS == null || parameterDTOS.isEmpty())
            return java.util.Collections.emptyList();

        List<ParameterEntity> updatedEntities = new ArrayList<>();
        // We use this map to quickly check which IDs were actually updated when building the MQTT payload
        java.util.Map<Long, Double> updatesMap = new java.util.HashMap<>();
        Long greenhouseId = null;

        // --- 1. Database Update Phase ---
        for (ParameterDTO dto : parameterDTOS) {
            if (dto.getId() == null || dto.getRequestedValue() == null)
                continue;

            ParameterEntity p = parameterRepository.findById(dto.getId())
                    .orElse(null);

            if (p != null) {
                // Update DB
                p.setRequestedValue(dto.getRequestedValue());
                parameterRepository.save(p);

                updatedEntities.add(p);
                updatesMap.put(p.getId(), dto.getRequestedValue());

                // Find Greenhouse ID (from the first valid parameter)
                if (greenhouseId == null) {
                    if (p.getGreenhouse() != null) {
                        greenhouseId = p.getGreenhouse()
                                .getId();
                    } else if (p.getZone() != null) {
                        greenhouseId = p.getZone()
                                .getGreenhouse()
                                .getId();
                    } else if (p.getFlowerpot() != null) {
                        greenhouseId = p.getFlowerpot()
                                .getZone()
                                .getGreenhouse()
                                .getId();
                    }
                }
            }
        }

        // --- 2. MQTT Command Phase ---
        if (greenhouseId != null) {
            Greenhouse greenhouse = greenhouseRepository.findById(greenhouseId)
                    .orElse(null);

            if (greenhouse != null && greenhouse.getIpAddress() != null) {
                try {
                    // Root: { "id": 1, "zones": [ ... ] }
                    java.util.Map<String, Object> rootPayload = new java.util.HashMap<>();
                    rootPayload.put("id", greenhouse.getId());
                    // Send other greenhouse metadata so it isn't lost if the ESP parses it
                    rootPayload.put("name", greenhouse.getName());

                    List<java.util.Map<String, Object>> zonesPayload = new ArrayList<>();

                    // Walk the full hierarchy to find parents of updated parameters
                    for (Zone z : greenhouse.getZones()) {
                        java.util.Map<String, Object> zoneMap = new java.util.HashMap<>();
                        zoneMap.put("id", z.getId());
                        zoneMap.put("name", z.getName()); // Include name

                        List<java.util.Map<String, Object>> zParams = new ArrayList<>();
                        List<java.util.Map<String, Object>> potsPayload = new ArrayList<>();
                        boolean zoneHasUpdates = false;

                        // A. Check Zone Parameters
                        if (z.getParameters() != null) {
                            for (ParameterEntity p : z.getParameters()) {
                                if (updatesMap.containsKey(p.getId())) {
                                    // FIX: Send full parameter object, not just ID/Value
                                    zParams.add(createFullParameterPayload(p, updatesMap.get(p.getId())));
                                    zoneHasUpdates = true;
                                }
                            }
                        }

                        // B. Check Flowerpot Parameters
                        if (z.getFlowerpots() != null) {
                            for (Flowerpot fp : z.getFlowerpots()) {
                                java.util.Map<String, Object> potMap = new java.util.HashMap<>();
                                potMap.put("id", fp.getId());
                                potMap.put("name", fp.getName()); // Include name

                                List<java.util.Map<String, Object>> fpParams = new ArrayList<>();
                                boolean potHasUpdates = false;

                                if (fp.getParameters() != null) {
                                    for (ParameterEntity p : fp.getParameters()) {
                                        if (updatesMap.containsKey(p.getId())) {
                                            // FIX: Send full parameter object
                                            fpParams.add(createFullParameterPayload(p, updatesMap.get(p.getId())));
                                            potHasUpdates = true;
                                            zoneHasUpdates = true;
                                        }
                                    }
                                }

                                if (potHasUpdates) {
                                    potMap.put("parameters", fpParams);
                                    potsPayload.add(potMap);
                                }
                            }
                        }

                        // Add Zone to payload only if it (or its children) changed
                        if (zoneHasUpdates) {
                            if (!zParams.isEmpty())
                                zoneMap.put("parameters", zParams);
                            if (!potsPayload.isEmpty())
                                zoneMap.put("flowerpots", potsPayload);
                            zonesPayload.add(zoneMap);
                        }
                    }

                    // Send MQTT if there is data
                    if (!zonesPayload.isEmpty()) {
                        rootPayload.put("zones", zonesPayload);

                        ObjectMapper mapper = new ObjectMapper();
                        String jsonPayload = mapper.writeValueAsString(rootPayload);

                        // Use specific topic structure
                        String topic = "greenhouse/" + greenhouse.getIpAddress() + "/set/model";
                        mqttService.sendCommand(topic, jsonPayload);
                    }

                } catch (Exception e) {
                    System.err.println("MQTT Send Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        // --- 3. Return DTOs ---
        return updatedEntities.stream()
                .map(parameterMapper::toDto)
                .toList();
    }

    /**
     * Helper to create a complete JSON map for a parameter.
     * Prevents the ESP8266 from overwriting metadata (unit, min, max) with defaults.
     */
    private java.util.Map<String, Object> createFullParameterPayload (ParameterEntity p, Double newRequestedValue) {
        java.util.Map<String, Object> pMap = new java.util.HashMap<>();
        pMap.put("id", p.getId());
        pMap.put("name", p.getName());
        pMap.put("mutable", p.isMutable());

        // Use the NEW requested value from the update
        pMap.put("requestedValue", newRequestedValue);

        // Send current value if available, else send whatever is in DB or null
        pMap.put("currentValue", p.getCurrentValue());

        pMap.put("min", p.getMin());
        pMap.put("max", p.getMax());
        pMap.put("unit", p.getUnit());

        // Handle Enum or String type safely
        if (p.getParameterType() != null) {
            pMap.put("parameterType", p.getParameterType()
                    .toString());
        } else {
            pMap.put("parameterType", "");
        }

        return pMap;
    }
}
