package com.greenhouse.greenhouse.controllers;

import com.greenhouse.greenhouse.services.ParameterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/parameter")
public class ParameterController {
    private final ParameterService parameterService;

    public ParameterController (ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteParameter (Long id) {
        parameterService.deleteParameter(id);
        return ResponseEntity.ok("Parameter with id " + id + " was deleted");
    }
}
