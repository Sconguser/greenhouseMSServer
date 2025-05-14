package com.greenhouse.greenhouse.services;

import com.greenhouse.greenhouse.dtos.RequirementDTO;
import com.greenhouse.greenhouse.exceptions.PlantNotFoundException;
import com.greenhouse.greenhouse.mappers.RequirementMapper;
import com.greenhouse.greenhouse.models.Plant;
import com.greenhouse.greenhouse.models.RequirementEntity;
import com.greenhouse.greenhouse.repositories.PlantRepository;
import com.greenhouse.greenhouse.repositories.RequirementRepository;
import org.springframework.stereotype.Service;

@Service
public class RequirementService {
    private final RequirementRepository requirementRepository;
    private final RequirementMapper requirementMapper;
    private final PlantRepository plantRepository;

    public RequirementService (RequirementRepository repository, RequirementMapper requirementMapper,
                               PlantRepository plantRepository)
    {
        this.requirementRepository = repository;
        this.requirementMapper = requirementMapper;
        this.plantRepository = plantRepository;
    }

    public RequirementDTO addRequirementToPlant (Long plantId, RequirementDTO requirementDTO) {
        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new PlantNotFoundException("Plant wiht id " + plantId + " was not found"));
        RequirementEntity entity = requirementMapper.toEntity(requirementDTO);
        entity.setPlant(plant);
        RequirementEntity saved = requirementRepository.save(entity);
        return requirementMapper.toDto(saved);
    }

    public void deleteRequirement (Long requirementId) {
        requirementRepository.deleteById(requirementId);
    }
}
