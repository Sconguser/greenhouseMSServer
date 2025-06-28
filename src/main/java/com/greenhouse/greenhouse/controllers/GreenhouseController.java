package com.greenhouse.greenhouse.controllers;

import com.greenhouse.greenhouse.dtos.ParameterDTO;
import com.greenhouse.greenhouse.requests.GreenhouseRequest;
import com.greenhouse.greenhouse.requests.ZoneRequest;
import com.greenhouse.greenhouse.responses.GreenhouseResponse;
import com.greenhouse.greenhouse.responses.ZoneResponse;
import com.greenhouse.greenhouse.services.GreenhouseService;
import com.greenhouse.greenhouse.services.ParameterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/greenhouse")
public class GreenhouseController {
    private final GreenhouseService greenhouseService;
    private final ParameterService parameterService;

    public GreenhouseController (GreenhouseService greenhouseService, ParameterService parameterService) {
        this.greenhouseService = greenhouseService;
        this.parameterService = parameterService;
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

    @PostMapping("/{id}/addZone")
    public ResponseEntity<ZoneResponse> addZone (@PathVariable Long id, @Valid @RequestBody ZoneRequest zoneRequest) {
        return ResponseEntity.ok(greenhouseService.addZone(id, zoneRequest));
    }

    @DeleteMapping("/{greenhouseId}/deleteZone/{zoneId}")
    public ResponseEntity<?> deleteZone (@PathVariable Long greenhouseId, @PathVariable Long zoneId) {
        greenhouseService.removeZone(greenhouseId, zoneId);
        return ResponseEntity.ok("Zone " + zoneId + " was deleted from greenhouse with id " + greenhouseId);
    }

    @PostMapping("/{id}/addParameter")
    public ResponseEntity<ParameterDTO> addParameter (@PathVariable Long id, @Valid @RequestBody ParameterDTO parameterDTO) {
        return ResponseEntity.ok(parameterService.addToGreenhouse(id, parameterDTO));
    }
    @GetMapping("/{id}/parameters")
    public ResponseEntity<List<ParameterDTO>> getParameters(@PathVariable Long id){
        return ResponseEntity.ok(parameterService.getGreenhouseParameters(id));
    }
}
