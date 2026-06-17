package com.greenhouse.greenhouse.controllers;

import com.greenhouse.greenhouse.dtos.PlantAlertDTO;
import com.greenhouse.greenhouse.services.PlantHealthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Plant-health alerts (the in-app feed).
 *
 * <pre>
 * GET    /plants/alerts/greenhouse/{id}?includeResolved=false
 * GET    /plants/alerts/greenhouse/{id}/count   → active count
 * DELETE /plants/alerts/{id}                     → dismiss one
 * </pre>
 */
@RestController
@RequestMapping("/plants/alerts")
public class PlantAlertController {

    private final PlantHealthService plantHealthService;

    public PlantAlertController(PlantHealthService plantHealthService) {
        this.plantHealthService = plantHealthService;
    }

    @GetMapping("/greenhouse/{id}")
    public ResponseEntity<List<PlantAlertDTO>> getAlerts(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean includeResolved) {
        return ResponseEntity.ok(plantHealthService.getAlerts(id, includeResolved));
    }

    @GetMapping("/greenhouse/{id}/count")
    public ResponseEntity<Long> getActiveCount(@PathVariable Long id) {
        return ResponseEntity.ok(plantHealthService.countActive(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> dismiss(@PathVariable Long id) {
        plantHealthService.dismiss(id);
        return ResponseEntity.noContent().build();
    }
}
