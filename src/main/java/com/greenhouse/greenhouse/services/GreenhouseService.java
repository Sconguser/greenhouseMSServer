package com.greenhouse.greenhouse.services;

import com.greenhouse.greenhouse.exceptions.GreenhouseNotFoundException;
import com.greenhouse.greenhouse.exceptions.PlantNotFoundException;
import com.greenhouse.greenhouse.mappers.GreenhouseMapper;
import com.greenhouse.greenhouse.mappers.PlantMapper;
import com.greenhouse.greenhouse.models.Greenhouse;
import com.greenhouse.greenhouse.models.Plant;
import com.greenhouse.greenhouse.repositories.GreenhouseRepository;
import com.greenhouse.greenhouse.repositories.PlantRepository;
import com.greenhouse.greenhouse.requests.GreenhouseRequest;
import com.greenhouse.greenhouse.requests.PlantRequest;
import com.greenhouse.greenhouse.responses.GreenhouseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GreenhouseService {
    private final GreenhouseRepository greenhouseRepository;
    private final PlantRepository plantRepository;
    private final GreenhouseMapper greenhouseMapper;
    private final PlantMapper plantMapper;

    @Autowired
    public GreenhouseService (GreenhouseRepository greenhouseRepository, PlantRepository plantRepository,
                              GreenhouseMapper greenhouseMapper, PlantMapper plantMapper)
    {
        this.greenhouseRepository = greenhouseRepository;
        this.plantRepository = plantRepository;
        this.greenhouseMapper = greenhouseMapper;
        this.plantMapper = plantMapper;
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
        greenhouseRepository.save(greenhouse);
        return greenhouseMapper.toResponse(greenhouse);
    }

    public void deleteGreenhouse (Long id) {
        greenhouseRepository.deleteById(id);
    }

    public GreenhouseResponse addPlantToGreenhouse (Long greenhouseId, Long plantId) {
        Greenhouse greenhouse = getGreenhouseEntity(greenhouseId);
        Optional<Plant> plantOpt = plantRepository.findById(plantId);

        if (plantOpt.isPresent()) {
            Plant plant = plantOpt.get();
            greenhouse.getPlants()
                    .add(plant);
            greenhouseRepository.save(greenhouse);
            return greenhouseMapper.toResponse(greenhouse);
        }
        throw new PlantNotFoundException("Plant " + plantId + " was not found in greenhouse " + greenhouseId);
    }

    public GreenhouseResponse deletePlantFromGreenhouse (Long greenhouseId, Long plantId) {
        Greenhouse greenhouse = getGreenhouseEntity(greenhouseId);
        Optional<Plant> plantToDelete = greenhouse.getPlants()
                .stream()
                .filter((plant) -> plant.getId()
                        .equals(plantId))
                .findFirst();
        if (plantToDelete.isPresent()) {
            greenhouse.getPlants()
                    .remove(plantToDelete.get());
            greenhouseRepository.save(greenhouse);
            return greenhouseMapper.toResponse(greenhouse);
        } else {
            throw new PlantNotFoundException("Did not find plant " + plantId + " in greenhouse " + greenhouseId);
        }
    }

    public Plant createOrFetchPlant (PlantRequest plantRequest) {
        return plantRepository.findByName(plantRequest.getName())
                .orElseGet(() -> {
                    Plant plant = plantMapper.toEntity(plantRequest);
                    plantRepository.save(plant);
                    return plant;
                });
    }
}
