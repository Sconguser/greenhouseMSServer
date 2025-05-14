package com.greenhouse.greenhouse.services;

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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParameterService {
    private ParameterMapper parameterMapper;
    private ParameterRepository parameterRepository;
    private GreenhouseRepository greenhouseRepository;
    private ZoneRepository zoneRepository;
    private FlowerpotRepository flowerpotRepository;

    public ParameterDTO addToGreenhouse (Long greenhouseId, ParameterDTO parameterDTO) {
        Greenhouse greenhouse = greenhouseRepository.findById(greenhouseId)
                .orElseThrow(
                        () -> new GreenhouseNotFoundException("Greenhouse with id " + greenhouseId + " was not found"));
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
        ParameterEntity entity = parameterMapper.toEntity(parameterDTO);
        entity.setGreenhouse(null);
        entity.setZone(null);
        entity.setFlowerpot(flowerpot);
        ParameterEntity saved = parameterRepository.save(entity);
        return parameterMapper.toDto(saved);
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
}
