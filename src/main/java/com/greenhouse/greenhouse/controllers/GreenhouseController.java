package com.greenhouse.greenhouse.controllers;

import com.greenhouse.greenhouse.models.Plant;
import com.greenhouse.greenhouse.requests.GreenhouseRequest;
import com.greenhouse.greenhouse.requests.PlantRequest;
import com.greenhouse.greenhouse.responses.GreenhouseResponse;
import com.greenhouse.greenhouse.services.GreenhouseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/greenhouse")
public class GreenhouseController {
    private final GreenhouseService greenhouseService;

    public GreenhouseController (GreenhouseService greenhouseService) {
        this.greenhouseService = greenhouseService;
    }

    @GetMapping("/{id}")
    public GreenhouseResponse getGreenhouse (@PathVariable Long id) {
        return greenhouseService.getGreenhouse(id);
    }

    @PostMapping("/add")
    public ResponseEntity<GreenhouseResponse> addGreenhouse (@Valid @RequestBody GreenhouseRequest request) {
        return ResponseEntity.ok(greenhouseService.addGreenhouse(request));
    }

    @PostMapping("/addPlant/{greenhouseId}")
    public ResponseEntity<String> addPlantToGreenhouse (@PathVariable Long greenhouseId,
                                                        @RequestBody PlantRequest plantRequest)
    {

        Plant storedPlant = greenhouseService.createOrFetchPlant(plantRequest);
        boolean success = greenhouseService.addPlantToGreenhouse(greenhouseId, storedPlant.getId());

        if (success) {
            return ResponseEntity.ok("Plant added to greenhouse successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Greenhouse not found.");
        }
    }

}
