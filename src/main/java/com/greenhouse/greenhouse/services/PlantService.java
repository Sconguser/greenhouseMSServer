package com.greenhouse.greenhouse.services;

import com.greenhouse.greenhouse.exceptions.PlantAlreadyExistsException;
import com.greenhouse.greenhouse.exceptions.PlantNotFoundException;
import com.greenhouse.greenhouse.mappers.PlantMapper;
import com.greenhouse.greenhouse.mappers.RequirementMapper;
import com.greenhouse.greenhouse.models.*;
import com.greenhouse.greenhouse.repositories.PlantRepository;
import com.greenhouse.greenhouse.requests.PlantRequest;
import com.greenhouse.greenhouse.responses.PlantResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlantService {
    private final PlantRepository plantRepository;
    private final PlantMapper plantMapper;
    private final RequirementMapper requirementMapper;

    @Autowired
    public PlantService (PlantRepository plantRepository, PlantMapper plantMapper, RequirementMapper requirementMapper)
    {
        this.plantRepository = plantRepository;
        this.plantMapper = plantMapper;
        this.requirementMapper = requirementMapper;
    }

    public PlantResponse getPlantById (Long id) {
        return plantRepository.findById(id)
                .map(plantMapper::toResponse)
                .orElseThrow(() -> new PlantNotFoundException("Plant not found"));
    }

    public List<PlantResponse> getAllPlants () {
        return plantRepository.findAll()
                .stream()
                .map(plantMapper::toResponse)
                .collect(Collectors.toList());
    }

    public void addPlant (PlantRequest plantRequest) {
        Optional<Plant> plantFromDatabase = plantRepository.findByName(plantRequest.getName());
        if(plantFromDatabase.isPresent()){
            throw new PlantAlreadyExistsException("Plant with this name already exists");
        }
        Plant plant = plantMapper.toEntity(plantRequest);
//        if (plantRequest.getImage_data() != null) {
//            plant.setImageData(Base64.getDecoder()
//                    .decode(plantRequest.getImage_data()));
//        }

        plantRepository.save(plant);
    }

    public void deletePlant (Long id) {
        plantRepository.deleteById(id);
    }

    public PlantResponse updatePlant (Long id, PlantRequest plantRequest) {
        Plant plant = plantRepository.findById(id)
                .orElseThrow(() -> new PlantNotFoundException("Plant not found"));

        if (plantRequest.getName() != null) {
            plant.setName(plantRequest.getName());
        }
        if (plantRequest.getDescription() != null) {
            plant.setDescription(plantRequest.getDescription());
        }
        if (plantRequest.getImageData() != null) {
            plant.setImageData(Base64.getDecoder()
                    .decode(plantRequest.getImageData()));
        }

        plantRepository.save(plant);
        return plantMapper.toResponse(plant);
    }


    /// TODO: poprawić af
    public boolean checkIfRequirementsAreMet (Long plantId, List<ParameterEntity> parameters) {
        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new PlantNotFoundException("Plant not found"));
        List<RequirementEntity> requirements = plant.getRequirements();
        requirements
                .forEach(requirement -> {
                    Optional<ParameterEntity> parameterOp = parameters.stream()
                            .filter(parameter -> parameter.getName()
                                    .equals(requirement.getName()))
                            .findFirst();
                });
        return true;
    }

}
