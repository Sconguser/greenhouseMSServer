package com.greenhouse.greenhouse.controllers;

import com.greenhouse.greenhouse.services.ZoneStatusService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/zoneStatus")
public class ZoneStatusController {
    private final ZoneStatusService zoneStatusService;

    public ZoneStatusController (ZoneStatusService zoneStatusService) {
        this.zoneStatusService = zoneStatusService;
    }

    @GetMapping("/{zoneId}")
    public ZoneStatusResponse getGreenhouseStatus (@PathVariable Long greenhouseId) {
        return zoneStatusService.getGreenhouseStatus(greenhouseId);
    }

    @PostMapping("/statusCheck/{zoneId}")
    public ResponseEntity<String> statusCheck (@PathVariable Long greenhouseId,
                                               @RequestBody ZoneStatusRequest greenhouseStatus)
    {
        zoneStatusService.processGreenhouseStatus(greenhouseId, greenhouseStatus);
        return ResponseEntity.ok("Status updated and correct");
    }

    @PatchMapping("/setTemperature/{zoneId}")
    public void setZoneTemperature (@PathVariable Long zoneId, @RequestParam double temperature) {
        zoneStatusService.setTemperature(zoneId, temperature);
    }

    @PatchMapping("/setHumidity/{zoneId}")
    public void setZoneHumidity (@PathVariable Long zoneId, @RequestParam double humidity) {
        zoneStatusService.setHumidity(zoneId, humidity);
    }

    @Bean
    public CommandLineRunner commandLineRunner (ApplicationContext ctx) {
        return args -> {
            System.out.println("Zone status controller initialized");
        };
    }

}
