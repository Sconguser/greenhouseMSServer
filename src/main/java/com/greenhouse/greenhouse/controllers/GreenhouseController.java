package com.greenhouse.greenhouse.controllers;

import com.greenhouse.greenhouse.models.Plant;
import com.greenhouse.greenhouse.requests.GreenhouseRequest;
import com.greenhouse.greenhouse.requests.PlantRequest;
import com.greenhouse.greenhouse.responses.GreenhouseResponse;
import com.greenhouse.greenhouse.services.GreenhouseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/greenhouse")
public class GreenhouseController {
    private final GreenhouseService greenhouseService;

    public GreenhouseController (GreenhouseService greenhouseService) {
        this.greenhouseService = greenhouseService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<GreenhouseResponse> getGreenhouse (@PathVariable Long id) {
        return ResponseEntity.ok(greenhouseService.getGreenhouse(id));
    }
    @GetMapping("/")
    public ResponseEntity<List<GreenhouseResponse>> getAllGreenhouses () {
        return ResponseEntity.ok(greenhouseService.getAllGreenhouses());
    }

    @PostMapping("/add")
    public ResponseEntity<GreenhouseResponse> addGreenhouse (@Valid @RequestBody GreenhouseRequest request) {
        return ResponseEntity.ok(greenhouseService.addGreenhouse(request));
    }

    @PatchMapping("/{greenhouseId}")
    public ResponseEntity<GreenhouseResponse> updateGreenhouse (@PathVariable Long greenhouseId,
                                                              @Valid @RequestBody GreenhouseRequest request)
    {
        return ResponseEntity.ok(greenhouseService.updateGreenhouse(request, greenhouseId));
    }

    @DeleteMapping("/{greenhouseId}")
    public ResponseEntity<?> deleteGreenhouse (@PathVariable Long greenhouseId) {
        greenhouseService.deleteGreenhouse(greenhouseId);
        return ResponseEntity.ok("Greenhouse " + greenhouseId + " deleted");
    }

    @PostMapping("/addPlant/{greenhouseId}")
    public ResponseEntity<GreenhouseResponse> addPlantToGreenhouse (@PathVariable Long greenhouseId,
                                                        @RequestBody PlantRequest plantRequest)
    {

        Plant storedPlant = greenhouseService.createOrFetchPlant(plantRequest);
        return ResponseEntity.ok(greenhouseService.addPlantToGreenhouse(greenhouseId, storedPlant.getId()));

    }

    @PatchMapping("/deletePlant/{greenhouseId}")
    public ResponseEntity<GreenhouseResponse> deletePlantFromGreenhouse (@PathVariable Long greenhouseId,
                                                                         @RequestParam Long plantId)
    {
        return ResponseEntity.ok(greenhouseService.deletePlantFromGreenhouse(greenhouseId, plantId));
    }

}
