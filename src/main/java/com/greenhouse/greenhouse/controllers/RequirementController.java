package com.greenhouse.greenhouse.controllers;

import com.greenhouse.greenhouse.services.RequirementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/requirement")
public class RequirementController {
    private final RequirementService requirementService;

    public RequirementController (RequirementService requirementService) {
        this.requirementService = requirementService;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRequirement (Long id) {
        requirementService.deleteRequirement(id);
        return ResponseEntity.ok("Requirement with id " + id + " was deleted");
    }
}
