package com.greenhouse.greenhouse.controllers;

import com.greenhouse.greenhouse.models.*;
import com.greenhouse.greenhouse.repositories.GreenhouseEventRepository;
import com.greenhouse.greenhouse.repositories.GreenhouseRepository;
import com.greenhouse.greenhouse.repositories.ParameterHistoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * ⚠ DEV / TEST ONLY — remove or secure before production deployment.
 *
 * <p>Bypasses MQTT and the {@code analyticsEnabled} flag to write synthetic
 * parameter-history rows and lifecycle events directly into the database.
 * Useful when the ESP8266 board is unavailable.</p>
 *
 * <pre>
 * POST /analytics/dev/seed
 *     ?greenhouseId=1          (omit to seed ALL greenhouses)
 *     &hours=24                (how far back the history should start, default 24)
 *     &intervalMinutes=10      (gap between generated readings, default 10)
 * </pre>
 */
@RestController
@RequestMapping("/analytics/dev")
public class AnalyticsSeedController {

    private final GreenhouseRepository greenhouseRepo;
    private final ParameterHistoryRepository historyRepo;
    private final GreenhouseEventRepository eventRepo;

    public AnalyticsSeedController(GreenhouseRepository greenhouseRepo,
                                   ParameterHistoryRepository historyRepo,
                                   GreenhouseEventRepository eventRepo) {
        this.greenhouseRepo = greenhouseRepo;
        this.historyRepo = historyRepo;
        this.eventRepo = eventRepo;
    }

    // ─── Seed endpoint ────────────────────────────────────────────────────────

    @PostMapping("/seed")
    @Transactional
    public ResponseEntity<Map<String, Object>> seed(
            @RequestParam(required = false) Long greenhouseId,
            @RequestParam(defaultValue = "24") int hours,
            @RequestParam(defaultValue = "10") int intervalMinutes)
    {
        if (hours < 1 || hours > 8760)
            return ResponseEntity.badRequest().body(Map.of("error", "'hours' must be 1-8760"));
        if (intervalMinutes < 1 || intervalMinutes > 1440)
            return ResponseEntity.badRequest().body(Map.of("error", "'intervalMinutes' must be 1-1440"));

        List<Greenhouse> targets = greenhouseId != null
                ? greenhouseRepo.findById(greenhouseId)
                        .map(List::of)
                        .orElse(List.of())
                : greenhouseRepo.findAll();

        if (targets.isEmpty())
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "No greenhouse found with id=" + greenhouseId));

        int totalHistory = 0;
        int totalEvents  = 0;
        List<Map<String, Object>> seeded = new ArrayList<>();

        for (Greenhouse gh : targets) {
            List<ParameterEntity> params = collectParameters(gh);
            if (params.isEmpty()) {
                seeded.add(Map.of("greenhouse", gh.getName(), "skipped", "no parameters"));
                continue;
            }

            int historyCount = seedHistory(gh, params, hours, intervalMinutes);
            int eventsCount  = seedEvents(gh, hours);
            totalHistory += historyCount;
            totalEvents  += eventsCount;

            seeded.add(Map.of(
                    "greenhouse", gh.getName(),
                    "parameters", params.stream().map(ParameterEntity::getName).toList(),
                    "historyRowsInserted", historyCount,
                    "eventsInserted", eventsCount
            ));
        }

        return ResponseEntity.ok(Map.of(
                "totalHistoryRowsInserted", totalHistory,
                "totalEventsInserted", totalEvents,
                "greenhouses", seeded
        ));
    }

    // ─── Delete seed data ─────────────────────────────────────────────────────

    /**
     * Convenience endpoint to wipe ALL analytics data (history + events).
     * Useful for resetting between test runs.
     */
    @DeleteMapping("/seed")
    @Transactional
    public ResponseEntity<Map<String, Object>> clearAll() {
        long historyCount = historyRepo.count();
        long eventsCount  = eventRepo.count();
        historyRepo.deleteAll();
        eventRepo.deleteAll();
        return ResponseEntity.ok(Map.of(
                "deletedHistoryRows", historyCount,
                "deletedEvents", eventsCount
        ));
    }

    // ─── History generation ───────────────────────────────────────────────────

    private int seedHistory(Greenhouse gh, List<ParameterEntity> params,
                             int hours, int intervalMinutes) {
        LocalDateTime end   = LocalDateTime.now();
        LocalDateTime start = end.minusHours(hours);
        long totalMinutes   = (long) hours * 60;
        int  steps          = (int) (totalMinutes / intervalMinutes);

        // Pre-build per-parameter wave configs
        List<WaveConfig> waves = new ArrayList<>();
        Random rng = new Random();
        for (ParameterEntity p : params) {
            waves.add(new WaveConfig(p, rng));
        }

        List<ParameterHistoryEntry> batch = new ArrayList<>(steps * params.size());

        for (int s = 0; s <= steps; s++) {
            LocalDateTime ts = start.plusMinutes((long) s * intervalMinutes);
            double progress  = (double) s / Math.max(steps, 1); // 0.0 → 1.0

            for (WaveConfig w : waves) {
                double value = w.valueAt(progress);
                ParameterHistoryEntry entry = new ParameterHistoryEntry();
                entry.setParameterId(w.param.getId());
                entry.setParameterName(w.param.getName());
                entry.setUnit(w.param.getUnit());
                entry.setGreenhouseId(gh.getId());
                entry.setValue(value);
                entry.setRecordedAt(ts);
                batch.add(entry);
            }
        }

        historyRepo.saveAll(batch);
        return batch.size();
    }

    // ─── Event generation ─────────────────────────────────────────────────────

    /**
     * Generates a realistic event sequence:
     *   BOOT at start → [CRASH → BOOT] × 1-3 times spread over the range → BOOT near end.
     */
    private int seedEvents(Greenhouse gh, int hours) {
        LocalDateTime now   = LocalDateTime.now();
        LocalDateTime start = now.minusHours(hours);
        Random rng = new Random(gh.getId()); // deterministic per greenhouse

        List<GreenhouseEvent> events = new ArrayList<>();

        // Initial boot
        events.add(new GreenhouseEvent(gh, GreenhouseEventType.BOOT,
                start.plusMinutes(rng.nextInt(5)), "Seed: initial boot"));

        // 1-3 crash/boot pairs spread evenly through the window
        int pairs = 1 + rng.nextInt(3);
        long segmentMinutes = (long) hours * 60 / (pairs + 1);

        for (int i = 1; i <= pairs; i++) {
            LocalDateTime crashTime = start
                    .plusMinutes(segmentMinutes * i + rng.nextInt((int) Math.max(segmentMinutes / 3, 1)));
            LocalDateTime bootTime  = crashTime.plusMinutes(5 + rng.nextInt(20));

            events.add(new GreenhouseEvent(gh, GreenhouseEventType.CRASH,
                    crashTime, "Seed: simulated crash"));
            if (bootTime.isBefore(now)) {
                events.add(new GreenhouseEvent(gh, GreenhouseEventType.BOOT,
                        bootTime, "Seed: recovery boot"));
            }
        }

        eventRepo.saveAll(events);
        return events.size();
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    /** Collects all parameters across the greenhouse's zones and flowerpots. */
    private List<ParameterEntity> collectParameters(Greenhouse gh) {
        List<ParameterEntity> all = new ArrayList<>(gh.getParameters());
        for (Zone zone : gh.getZones()) {
            all.addAll(zone.getParameters());
            for (Flowerpot fp : zone.getFlowerpots()) {
                all.addAll(fp.getParameters());
            }
        }
        // Keep only parameters that have a valid id
        all.removeIf(p -> p.getId() == null);
        return all;
    }

    // ─── Wave config ──────────────────────────────────────────────────────────

    /**
     * Generates a smooth oscillating value for one parameter, bounded by its
     * min/max. Falls back to 0-100 when min/max are not set.
     *
     * <ul>
     *   <li>VALUE parameters: sine wave + random noise</li>
     *   <li>TOGGLE parameters: alternates 0/1 every ~15 steps</li>
     * </ul>
     */
    private static class WaveConfig {
        final ParameterEntity param;
        final double lo;
        final double hi;
        final double phaseOffset;   // radians — each param gets a unique phase
        final double frequency;     // cycles over the whole window
        final double noiseScale;
        final Random rng;

        WaveConfig(ParameterEntity p, Random rng) {
            this.param  = p;
            this.rng    = new Random(p.getId());  // deterministic per parameter
            this.lo     = p.getMin()  != null ? p.getMin()  : 0.0;
            this.hi     = p.getMax()  != null ? p.getMax()  : 100.0;
            this.phaseOffset = rng.nextDouble() * 2 * Math.PI;
            // 1-3 full cycles over the window
            this.frequency   = 1.0 + rng.nextInt(3);
            this.noiseScale  = (hi - lo) * 0.04;  // ±4% noise
        }

        double valueAt(double progress) {
            if (param.getParameterType() == ParameterType.TOGGLE) {
                // Flip every ~15 steps
                return ((int)(progress * 100) / 15) % 2 == 0 ? 0.0 : 1.0;
            }
            double mid    = (lo + hi) / 2.0;
            double amp    = (hi - lo) / 2.0;
            double wave   = mid + amp * Math.sin(2 * Math.PI * frequency * progress + phaseOffset);
            double noise  = (rng.nextDouble() * 2 - 1) * noiseScale;
            // Clamp to [lo, hi]
            return Math.max(lo, Math.min(hi, wave + noise));
        }
    }
}