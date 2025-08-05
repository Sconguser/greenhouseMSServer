package com.greenhouse.greenhouse.controllers;

import com.greenhouse.greenhouse.dtos.ParameterDTO;
import com.greenhouse.greenhouse.services.ParameterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    @PatchMapping("/updateParameters")
    public ResponseEntity<List<ParameterDTO>> updateParameters (@Valid @RequestBody List<ParameterDTO> parameterDTOs) {
        return ResponseEntity.ok(parameterService.updateParameters(parameterDTOs));
    }

}
