package com.greenhouse.greenhouse.controllers;

import com.greenhouse.greenhouse.dtos.RequirementDTO;
import com.greenhouse.greenhouse.requests.PlantRequest;
import com.greenhouse.greenhouse.responses.PlantResponse;
import com.greenhouse.greenhouse.services.PlantService;
import com.greenhouse.greenhouse.services.RequirementService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/plants")
public class PlantController {
    private final PlantService plantService;
    private final RequirementService requirementService;

    public PlantController (PlantService plantService, RequirementService requirementService) {
        this.plantService = plantService;
        this.requirementService = requirementService;
    }

    @GetMapping("/{id}")
    public PlantResponse getPlant (@PathVariable Long id) {
        return plantService.getPlantById(id);
    }

    @GetMapping("/")
    public List<PlantResponse> getAllPlants () {
        return plantService.getAllPlants();
    }

    @PostMapping("/addPlant")
    public void addPlant (@RequestBody PlantRequest plantRequest) {
        plantService.addPlant(plantRequest);
    }

    @DeleteMapping("/{id}")
    public void deletePlant (@PathVariable Long id) {
        plantService.deletePlant(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PlantResponse> updatePlant (@PathVariable Long id, @RequestBody PlantRequest plantRequest) {
        return ResponseEntity.ok(plantService.updatePlant(id, plantRequest));
    }

    @PostMapping("/{plantId}/addRequirement")
    public ResponseEntity<RequirementDTO> addRequirement (@PathVariable Long plantId,
                                                          @RequestBody @Valid RequirementDTO requirementDTO)
    {
        return ResponseEntity.ok(requirementService.addRequirementToPlant(plantId, requirementDTO));
    }

    @Bean
    public CommandLineRunner plantCommandLineRunner (ApplicationContext ctx) {
        return args -> {
            System.out.println("Plant controller initialized");
        };
    }
}
