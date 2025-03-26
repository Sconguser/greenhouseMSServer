package com.greenhouse.greenhouse.services;

import com.greenhouse.greenhouse.exceptions.PlantRequirementsNotMetException;
import com.greenhouse.greenhouse.models.Greenhouse;
import com.greenhouse.greenhouse.models.GreenhouseStatus;
import com.greenhouse.greenhouse.models.Plant;
import org.springframework.stereotype.Service;

@Service
public class GreenhouseManager {
    private final GreenhouseService greenhouseService;

    public GreenhouseManager (GreenhouseService greenhouseService) {
        this.greenhouseService = greenhouseService;
    }

    public void checkPlantRequirements (Greenhouse greenhouse, GreenhouseStatus greenhouseStatus) {
        double temperature = greenhouseStatus.getTemperature();
        double humidity = greenhouseStatus.getHumidity();
        double soilHumidity = greenhouseStatus.getSoilHumidity();
        for (Plant plant : greenhouse.getPlants()) {
            if (temperature > plant.getMaxTemperature() ||
                    temperature < plant.getMinTemperature() ||
                    humidity > plant.getMaxHumidity() ||
                    humidity < plant.getMinHumidity() ||
                    soilHumidity < plant.getMinSoilHumidity() ||
                    soilHumidity > plant.getMaxSoilHumidity())
            {
                throw new PlantRequirementsNotMetException(composeErrorMessage(greenhouse, greenhouseStatus, plant));
            }
        }
    }

    public Greenhouse getGreenhouseEntity (Long greenhouseId) {
        return greenhouseService.getGreenhouseEntity(greenhouseId);
    }

    private String composeErrorMessage (Greenhouse greenhouse, GreenhouseStatus greenhouseStatus,
                                        Plant plant)
    {
        return "There is a problem in " + greenhouse + " with current status " + greenhouseStatus + " with plant: " + plant;
    }
}
