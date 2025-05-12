package com.greenhouse.greenhouse.services;

import com.greenhouse.greenhouse.exceptions.FlowerpotNotFoundException;
import com.greenhouse.greenhouse.exceptions.ZoneNotFoundException;
import com.greenhouse.greenhouse.mappers.FlowerpotMapper;
import com.greenhouse.greenhouse.mappers.ZoneMapper;
import com.greenhouse.greenhouse.models.Flowerpot;
import com.greenhouse.greenhouse.models.Zone;
import com.greenhouse.greenhouse.repositories.FlowerpotRepository;
import com.greenhouse.greenhouse.repositories.ZoneRepository;
import com.greenhouse.greenhouse.requests.FlowerpotRequest;
import com.greenhouse.greenhouse.responses.FlowerpotResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZoneService {
    private ZoneRepository zoneRepository;
    private FlowerpotRepository flowerpotRepository;
    private ZoneMapper zoneMapper;
    private FlowerpotMapper flowerpotMapper;

    @Autowired
    public ZoneService (ZoneRepository zoneRepository, FlowerpotRepository flowerpotRepository, ZoneMapper zoneMapper,
                        FlowerpotMapper flowerpotMapper)
    {
        this.zoneRepository = zoneRepository;
        this.flowerpotRepository = flowerpotRepository;
        this.zoneMapper = zoneMapper;
        this.flowerpotMapper = flowerpotMapper;
    }

    public FlowerpotResponse addFlowerpot (Long zoneId, FlowerpotRequest flowerpotRequest) {
        Zone zone = getZone(zoneId);
        Flowerpot flowerpot = flowerpotMapper.toEntity(flowerpotRequest);
        zone.addFlowerpot(flowerpot);
        zoneRepository.save(zone);
        return flowerpotMapper.toResponse(flowerpot);
    }

    private Zone getZone (Long zoneId) {
        return zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ZoneNotFoundException("Zone " + zoneId + " was not found"));
    }

    public List<FlowerpotResponse> getAllFlowerpots (Long zoneId) {
        return getZone(zoneId).getFlowerpots()
                .stream()
                .map(flowerpot -> flowerpotMapper.toResponse(flowerpot))
                .toList();
    }

    public FlowerpotResponse getFlowerpot (Long zoneId, Long flowerpotId) {
        return flowerpotMapper.toResponse(getZone(zoneId).getFlowerpots()
                .stream()
                .filter(flowerpot -> flowerpot.getId()
                        .equals(flowerpotId))
                .findFirst()
                .orElseThrow(
                        () -> new FlowerpotNotFoundException("Flowerpot with id " + flowerpotId + " was not found")));
    }

    public void deleteFlowerpot (Long zoneId, Long flowerpotId) {
        Zone zone = getZone(zoneId);
        zone.deleteFlowerpot(flowerpotId);
        zoneRepository.save(zone);
    }

}
