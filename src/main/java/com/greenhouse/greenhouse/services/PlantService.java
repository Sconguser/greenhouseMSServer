package com.greenhouse.greenhouse.services;

import com.greenhouse.greenhouse.exceptions.PlantAlreadyExistsException;
import com.greenhouse.greenhouse.exceptions.PlantNotFoundException;
import com.greenhouse.greenhouse.mappers.PlantMapper;
import com.greenhouse.greenhouse.models.Plant;
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

    @Autowired
    public PlantService (PlantRepository plantRepository, PlantMapper plantMapper) {
        this.plantRepository = plantRepository;
        this.plantMapper = plantMapper;
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
        if (plantRequest.getMinTemperature() != null) {
            plant.setMinTemperature(plantRequest.getMinTemperature());
        }
        if (plantRequest.getMaxTemperature() != null) {
            plant.setMaxTemperature(plantRequest.getMaxTemperature());
        }
        if (plantRequest.getMinHumidity() != null) {
            plant.setMinHumidity(plantRequest.getMinHumidity());
        }
        if (plantRequest.getMaxHumidity() != null) {
            plant.setMinHumidity(plantRequest.getMaxHumidity());
        }
        if (plantRequest.getImageData() != null) {
            plant.setImageData(Base64.getDecoder()
                    .decode(plantRequest.getImageData()));
        }

        plantRepository.save(plant);
        return plantMapper.toResponse(plant);
    }

}
