package com.greenhouse.greenhouse.controllers;

import com.greenhouse.greenhouse.requests.PlantRequest;
import com.greenhouse.greenhouse.responses.PlantResponse;
import com.greenhouse.greenhouse.services.PlantService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plants")
public class PlantController {
    private final PlantService plantService;

    public PlantController (PlantService plantService) {
        this.plantService = plantService;
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

    @Bean
    public CommandLineRunner plantCommandLineRunner (ApplicationContext ctx) {
        return args -> {
            System.out.println("Plant controller initialized");
        };
    }
}
