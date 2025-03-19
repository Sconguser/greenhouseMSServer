package com.greenhouse.greenhouse.controllers;

import com.greenhouse.greenhouse.models.Status;
import com.greenhouse.greenhouse.requests.GreenhouseStatusRequest;
import com.greenhouse.greenhouse.responses.GreenhouseStatusResponse;
import com.greenhouse.greenhouse.services.GreenhouseStatusService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/greenhouseStatus")
public class GreenhouseStatusController {
    private final GreenhouseStatusService greenhouseStatusService;

    public GreenhouseStatusController (GreenhouseStatusService greenhouseStatusService) {
        this.greenhouseStatusService = greenhouseStatusService;
    }

    @GetMapping("/{greenhouseId}")
    public GreenhouseStatusResponse getGreenhouseStatus (@PathVariable Long greenhouseId) {
        return greenhouseStatusService.getGreenhouseStatus(greenhouseId);
    }

    @PostMapping("/statusCheck/{greenhouseId}")
    public ResponseEntity<String> statusCheck (@PathVariable Long greenhouseId,
                                               @RequestBody GreenhouseStatusRequest greenhouseStatus)
    {
        greenhouseStatusService.processGreenhouseStatus(greenhouseId, greenhouseStatus);
        return ResponseEntity.ok("Status updated and correct");
    }

    @PatchMapping("/setStatus/{greenhouseId}")
    public void setGreenhouseStatus (@PathVariable Long greenhouseId, @RequestParam Status status) {
        greenhouseStatusService.setStatus(greenhouseId, status);
    }

    @PatchMapping("/setTemperature/{greenhouseId}")
    public void setGreenhouseTemperature (@PathVariable Long greenhouseId, @RequestParam double temperature) {
        greenhouseStatusService.setTemperature(greenhouseId, temperature);
    }

    @PatchMapping("/setHumidity/{greenhouseId}")
    public void setGreenhouseHumidity (@PathVariable Long greenhouseId, @RequestParam double humidity) {
        greenhouseStatusService.setHumidity(greenhouseId, humidity);
    }

    @Bean
    public CommandLineRunner commandLineRunner (ApplicationContext ctx) {
        return args -> {
            System.out.println("Greenhouse status controller initialized");
        };
    }

}
