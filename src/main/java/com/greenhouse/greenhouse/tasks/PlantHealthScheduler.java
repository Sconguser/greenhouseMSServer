package com.greenhouse.greenhouse.tasks;

import com.greenhouse.greenhouse.services.PlantHealthService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Runs the plant-health requirement check once a minute. */
@Component
public class PlantHealthScheduler {

    private final PlantHealthService plantHealthService;

    public PlantHealthScheduler(PlantHealthService plantHealthService) {
        this.plantHealthService = plantHealthService;
    }

    @Scheduled(fixedRate = 60000)
    public void check() {
        try {
            plantHealthService.checkAllPlants();
        } catch (Exception e) {
            System.err.println("[PLANT HEALTH] Check failed: " + e.getMessage());
        }
    }
}
