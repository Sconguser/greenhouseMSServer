package com.greenhouse.greenhouse.controllers;

import com.greenhouse.greenhouse.dtos.ParameterDTO;
import com.greenhouse.greenhouse.requests.FlowerpotRequest;
import com.greenhouse.greenhouse.responses.FlowerpotResponse;
import com.greenhouse.greenhouse.services.ParameterService;
import com.greenhouse.greenhouse.services.ZoneService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/zone")
public class ZoneController {
    private final ZoneService zoneService;
    private final ParameterService parameterService;

    public ZoneController (ZoneService zoneService, ParameterService parameterService) {
        this.zoneService = zoneService;
        this.parameterService = parameterService;
    }

    @PostMapping("/{id}/addFlowerpot")
    public ResponseEntity<FlowerpotResponse> addFlowerpot (@PathVariable Long id,
                                                           @Valid @RequestBody FlowerpotRequest flowerpotRequest)
    {
        return ResponseEntity.ok(zoneService.addFlowerpot(id, flowerpotRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<FlowerpotResponse>> getAllFlowerpots (@PathVariable Long zoneId) {
        return ResponseEntity.ok(zoneService.getAllFlowerpots(zoneId));
    }

    @GetMapping("/{zoneId}/{flowerpotId}")
    public ResponseEntity<FlowerpotResponse> getFlowerpot (@PathVariable Long zoneId, @PathVariable Long flowerpotId) {
        return ResponseEntity.ok(zoneService.getFlowerpot(zoneId, flowerpotId));
    }


    @DeleteMapping("/{zoneId}/{flowerpotId}")
    public ResponseEntity<?> deleteFlowerpot (@PathVariable Long zoneId, @PathVariable Long flowerpotId) {
        zoneService.deleteFlowerpot(zoneId, flowerpotId);
        return ResponseEntity.ok("Flowerpot " + flowerpotId + " was deleted from zone with id " + zoneId);
    }

    @PostMapping("/{id}/addParameter")
    public ResponseEntity<ParameterDTO> addParameter (@PathVariable Long id,
                                                      @Valid @RequestBody ParameterDTO parameterDTO)
    {
        return ResponseEntity.ok(parameterService.addToZone(id, parameterDTO));
    }

    @GetMapping("/{id}/parameters")
    public ResponseEntity<List<ParameterDTO>> getParameters (@PathVariable Long id) {
        return ResponseEntity.ok(parameterService.getZoneParameters(id));
    }
}
