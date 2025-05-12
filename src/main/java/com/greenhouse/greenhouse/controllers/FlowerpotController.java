package com.greenhouse.greenhouse.controllers;

import com.greenhouse.greenhouse.requests.FlowerpotRequest;
import com.greenhouse.greenhouse.responses.FlowerpotResponse;
import com.greenhouse.greenhouse.services.FlowerpotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/flowerpot")
public class FlowerpotController {
    private final FlowerpotService flowerpotService;

    public FlowerpotController (FlowerpotService flowerpotService) {
        this.flowerpotService = flowerpotService;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FlowerpotResponse> updateFlowerpot (@PathVariable Long id,
                                                              @Valid @RequestBody FlowerpotRequest request)
    {
        return ResponseEntity.ok(flowerpotService.updateFlowerpot(id, request));
    }

    @PostMapping("/{flowerpotId}/{plantId}")
    public ResponseEntity<?> addPlant (@PathVariable Long id, @PathVariable Long plantId)
    {
        flowerpotService.addPlant(id, plantId);
        return ResponseEntity.ok("Plant added");
    }

}
