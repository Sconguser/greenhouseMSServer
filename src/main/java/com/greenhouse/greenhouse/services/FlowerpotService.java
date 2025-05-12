package com.greenhouse.greenhouse.services;

import com.greenhouse.greenhouse.exceptions.FlowerpotNotFoundException;
import com.greenhouse.greenhouse.exceptions.PlantNotFoundException;
import com.greenhouse.greenhouse.mappers.FlowerpotMapper;
import com.greenhouse.greenhouse.models.Flowerpot;
import com.greenhouse.greenhouse.models.Plant;
import com.greenhouse.greenhouse.repositories.FlowerpotRepository;
import com.greenhouse.greenhouse.repositories.PlantRepository;
import com.greenhouse.greenhouse.requests.FlowerpotRequest;
import com.greenhouse.greenhouse.responses.FlowerpotResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlowerpotService {
    private final FlowerpotRepository flowerpotRepository;
    private final FlowerpotMapper flowerpotMapper;
    private final PlantRepository plantRepository;

    @Autowired
    public FlowerpotService (FlowerpotRepository flowerpotRepository, FlowerpotMapper flowerpotMapper,
                             PlantRepository plantRepository)
    {
        this.flowerpotRepository = flowerpotRepository;
        this.flowerpotMapper = flowerpotMapper;
        this.plantRepository = plantRepository;
    }

    public void addPlant (Long flowerpotId, Long plantId) {
        Flowerpot flowerpot = flowerpotRepository.findById(flowerpotId)
                .orElseThrow(() -> new FlowerpotNotFoundException("Flowerpot with id " + flowerpotId + " not found"));
        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new PlantNotFoundException("Plant with id " + plantId + " not found"));
        flowerpot.addPlant(plant);
    }

    public FlowerpotResponse updateFlowerpot (Long id, FlowerpotRequest flowerpotRequest) {
        Flowerpot flowerPot = flowerpotRepository.findById(id)
                .orElseThrow(() -> new FlowerpotNotFoundException("Flowerpot with id " + id + " not found"));
        if (flowerpotRequest.getName() != null) {
            flowerPot.setName(flowerpotRequest.getName());
        }
        flowerpotRepository.save(flowerPot);
        return flowerpotMapper.toResponse(flowerPot);
    }
}
