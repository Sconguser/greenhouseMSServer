package com.greenhouse.greenhouse.services;

import com.greenhouse.greenhouse.models.Greenhouse;
import org.springframework.stereotype.Service;

@Service
public class GreenhouseManager {
    private final GreenhouseService greenhouseService;

    public GreenhouseManager (GreenhouseService greenhouseService) {
        this.greenhouseService = greenhouseService;
    }


    //TODO: implement
    public void checkPlantRequirements (Greenhouse greenhouse) {
    }

    public Greenhouse getGreenhouseEntity (Long greenhouseId) {
        return greenhouseService.getGreenhouseEntity(greenhouseId);
    }
}
