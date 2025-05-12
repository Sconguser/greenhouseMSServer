package com.greenhouse.greenhouse.controllers;

import com.greenhouse.greenhouse.requests.FlowerpotRequest;
import com.greenhouse.greenhouse.responses.FlowerpotResponse;
import com.greenhouse.greenhouse.services.ZoneService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/zone")
public class ZoneController {
    private final ZoneService zoneService;

    public ZoneController (ZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @PostMapping("/addFlowerpot/{id}")
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
}
