package com.greenhouse.greenhouse.controllers;

import com.greenhouse.greenhouse.dtos.ParameterDTO;
import com.greenhouse.greenhouse.requests.FlowerpotRequest;
import com.greenhouse.greenhouse.responses.FlowerpotResponse;
import com.greenhouse.greenhouse.services.FlowerpotService;
import com.greenhouse.greenhouse.services.ParameterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/flowerpot")
public class FlowerpotController {
    private final FlowerpotService flowerpotService;
    private final ParameterService parameterService;

    public FlowerpotController (FlowerpotService flowerpotService, ParameterService parameterService) {
        this.flowerpotService = flowerpotService;
        this.parameterService = parameterService;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FlowerpotResponse> updateFlowerpot (@PathVariable Long id,
                                                              @Valid @RequestBody FlowerpotRequest request)
    {
        return ResponseEntity.ok(flowerpotService.updateFlowerpot(id, request));
    }

    @PutMapping("/{flowerpotId}/addPlant/{plantId}")
    public ResponseEntity<?> addPlant (@PathVariable Long flowerpotId, @PathVariable Long plantId)
    {
        flowerpotService.addPlant(flowerpotId, plantId);
        return ResponseEntity.ok("Plant added");
    }

    @DeleteMapping("/{flowerpotId}/deletePlant/{plantId}")
    public ResponseEntity<?> deletePlant(@PathVariable Long flowerpotId, @PathVariable Long plantId){
        flowerpotService.deletePlant(flowerpotId, plantId);
        return ResponseEntity.ok("Plant deleted");
    }

    @PostMapping("/{id}/addParameter")
    public ResponseEntity<ParameterDTO> addParameter (@PathVariable Long id,
                                                      @Valid @RequestBody ParameterDTO parameterDTO)
    {
        return ResponseEntity.ok(parameterService.addToFlowerpot(id, parameterDTO));
    }

    @GetMapping("/{id}/parameters")
    public ResponseEntity<List<ParameterDTO>> getParameters (@PathVariable Long id) {
        return ResponseEntity.ok(parameterService.getFlowerpotParameters(id));
    }


}
