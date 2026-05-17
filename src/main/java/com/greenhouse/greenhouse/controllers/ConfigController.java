package com.greenhouse.greenhouse.controllers;

import com.greenhouse.greenhouse.dtos.telemetry.DeviceConfigDTO;
import com.greenhouse.greenhouse.dtos.telemetry.MappingDTO;
import com.greenhouse.greenhouse.services.ConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/greenhouse/{id}/config")
public class ConfigController {

    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    // -------------------------------------------------------------------------
    // Device config — physical sensor/actuator hardware list
    // Pushed to: greenhouse/{IP}/set/config  →  Arduino saves /config.json + reboots
    // -------------------------------------------------------------------------

    @GetMapping("/devices")
    public ResponseEntity<List<DeviceConfigDTO>> getDeviceConfig(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(configService.getDeviceConfig(id));
    }

    @PostMapping("/devices")
    public ResponseEntity<?> pushDeviceConfig(@PathVariable Long id,
                                              @RequestBody List<DeviceConfigDTO> dtos) throws Exception {
        configService.saveAndPushDeviceConfig(id, dtos);
        return ResponseEntity.ok("Device config saved and pushed to greenhouse " + id);
    }

    // -------------------------------------------------------------------------
    // Mapping config — sensor-to-parameter bindings + control logic
    // Pushed to: greenhouse/{IP}/set/mapping  →  Arduino saves /mapping.json + reboots
    // -------------------------------------------------------------------------

    @GetMapping("/mapping")
    public ResponseEntity<List<MappingDTO>> getMappingConfig(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(configService.getMappingConfig(id));
    }

    @PostMapping("/mapping")
    public ResponseEntity<?> pushMappingConfig(@PathVariable Long id,
                                               @RequestBody List<MappingDTO> dtos) throws Exception {
        configService.saveAndPushMappingConfig(id, dtos);
        return ResponseEntity.ok("Mapping config saved and pushed to greenhouse " + id);
    }
}