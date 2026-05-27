package com.greenhouse.greenhouse.controllers;

import com.greenhouse.greenhouse.dtos.analytics.AnalyticsSettingsDTO;
import com.greenhouse.greenhouse.dtos.analytics.GreenhouseEventDTO;
import com.greenhouse.greenhouse.dtos.analytics.GreenhouseStatsDTO;
import com.greenhouse.greenhouse.dtos.analytics.ParameterHistoryResponseDTO;
import com.greenhouse.greenhouse.services.AnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST endpoints for greenhouse analytics and crashlytics data.
 *
 * <pre>
 * GET  /analytics/greenhouse/{id}/events?from=&to=
 * GET  /analytics/greenhouse/{id}/stats?from=&to=
 * GET  /analytics/parameter/{id}/history?from=&to=&maxPoints=200
 * GET  /analytics/settings
 * PUT  /analytics/settings
 * </pre>
 */
@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    // ─── Crashlytics ──────────────────────────────────────────────────────────────

    @GetMapping("/greenhouse/{id}/events")
    public ResponseEntity<List<GreenhouseEventDTO>> getEvents(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(analyticsService.getGreenhouseEvents(id, from, to));
    }

    @GetMapping("/greenhouse/{id}/stats")
    public ResponseEntity<GreenhouseStatsDTO> getStats(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(analyticsService.getGreenhouseStats(id, from, to));
    }

    // ─── Parameter history ────────────────────────────────────────────────────────

    /**
     * @param maxPoints Max points returned; uniformly downsampled when exceeded (default 200).
     */
    @GetMapping("/parameter/{id}/history")
    public ResponseEntity<ParameterHistoryResponseDTO> getHistory(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "200") int maxPoints) {
        return ResponseEntity.ok(analyticsService.getParameterHistory(id, from, to, maxPoints));
    }

    // ─── Settings ─────────────────────────────────────────────────────────────────

    @GetMapping("/settings")
    public ResponseEntity<AnalyticsSettingsDTO> getSettings() {
        return ResponseEntity.ok(analyticsService.getSettings());
    }

    /**
     * Updates retention periods and cleanup interval.
     * The {@code lastCleanup} field is read-only and ignored in the request body.
     */
    @PutMapping("/settings")
    public ResponseEntity<AnalyticsSettingsDTO> updateSettings(
            @RequestBody AnalyticsSettingsDTO dto) {
        return ResponseEntity.ok(analyticsService.updateSettings(dto));
    }
}