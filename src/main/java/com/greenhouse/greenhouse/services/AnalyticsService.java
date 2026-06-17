package com.greenhouse.greenhouse.services;

import com.greenhouse.greenhouse.dtos.analytics.AnalyticsSettingsDTO;
import com.greenhouse.greenhouse.dtos.analytics.DeviceLogDTO;
import com.greenhouse.greenhouse.dtos.analytics.GreenhouseEventDTO;
import com.greenhouse.greenhouse.dtos.analytics.GreenhouseStatsDTO;
import com.greenhouse.greenhouse.dtos.analytics.ParameterHistoryPointDTO;
import com.greenhouse.greenhouse.dtos.analytics.ParameterHistoryResponseDTO;
import com.greenhouse.greenhouse.dtos.telemetry.TelemetryFlowerpotDTO;
import com.greenhouse.greenhouse.dtos.telemetry.TelemetryGreenhouseDTO;
import com.greenhouse.greenhouse.dtos.telemetry.TelemetryParameterDTO;
import com.greenhouse.greenhouse.dtos.telemetry.TelemetryZoneDTO;
import com.greenhouse.greenhouse.models.*;
import com.greenhouse.greenhouse.repositories.AnalyticsSettingsRepository;
import com.greenhouse.greenhouse.repositories.DeviceLogRepository;
import com.greenhouse.greenhouse.repositories.GreenhouseEventRepository;
import com.greenhouse.greenhouse.repositories.GreenhouseRepository;
import com.greenhouse.greenhouse.repositories.ParameterHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnalyticsService {

    private final GreenhouseEventRepository eventRepo;
    private final ParameterHistoryRepository historyRepo;
    private final DeviceLogRepository deviceLogRepo;
    private final GreenhouseRepository greenhouseRepository;
    private final AnalyticsSettingsRepository settingsRepo;

    @Autowired
    public AnalyticsService(GreenhouseEventRepository eventRepo,
                             ParameterHistoryRepository historyRepo,
                             DeviceLogRepository deviceLogRepo,
                             GreenhouseRepository greenhouseRepository,
                             AnalyticsSettingsRepository settingsRepo) {
        this.eventRepo = eventRepo;
        this.historyRepo = historyRepo;
        this.deviceLogRepo = deviceLogRepo;
        this.greenhouseRepository = greenhouseRepository;
        this.settingsRepo = settingsRepo;
    }

    // ─── Enabled guard ────────────────────────────────────────────────────────────

    /** Returns {@code true} when analytics collection is currently active. */
    public boolean isEnabled() {
        return getOrCreateSettings().isAnalyticsEnabled();
    }

    // ─── Event recording ─────────────────────────────────────────────────────────

    /**
     * Records a BOOT event — call this when a device transitions from
     * OFF/NOT_RESPONSIVE → ON (first telemetry after being offline).
     */
    @Transactional
    public void recordBoot(Long greenhouseId) {
        if (!isEnabled()) return;
        recordEvent(greenhouseId, GreenhouseEventType.BOOT, "Device reconnected");
    }

    /**
     * Records a CRASH event — call this when the status scheduler marks a
     * previously-ON device as NOT_RESPONSIVE (no telemetry for > 5 min).
     */
    @Transactional
    public void recordCrash(Long greenhouseId) {
        if (!isEnabled()) return;
        recordEvent(greenhouseId, GreenhouseEventType.CRASH, "No telemetry for 5 min");
    }

    private void recordEvent(Long greenhouseId, GreenhouseEventType type, String details) {
        greenhouseRepository.findById(greenhouseId).ifPresent(gh -> {
            GreenhouseEvent event = new GreenhouseEvent(gh, type, LocalDateTime.now(), details);
            eventRepo.save(event);
            System.out.printf("[ANALYTICS] %s event for greenhouse %d (%s)%n",
                    type, greenhouseId, gh.getName());
        });
    }

    /**
     * Removes all analytics rows (events + parameter history) belonging to a
     * greenhouse. Must run before the greenhouse itself is deleted, otherwise
     * the {@code greenhouse_event.greenhouse_id} foreign key blocks the delete.
     */
    @Transactional
    public void purgeGreenhouseData(Long greenhouseId) {
        int events = eventRepo.deleteByGreenhouseId(greenhouseId);
        int history = historyRepo.deleteByGreenhouseId(greenhouseId);
        int logs = deviceLogRepo.deleteByGreenhouseId(greenhouseId);
        System.out.printf("[ANALYTICS] Purged %d events, %d history rows and %d logs for greenhouse %d%n",
                events, history, logs, greenhouseId);
    }

    // ─── Device logs ──────────────────────────────────────────────────────────────

    /** Persists a device-emitted log line. Resolves the greenhouse by its IP. */
    @Transactional
    public void recordDeviceLogByIp(String ip, String level, String code, String message,
                                    Integer freeHeap, Long deviceUptime) {
        greenhouseRepository.findByIpAddress(ip).ifPresentOrElse(
                gh -> recordDeviceLog(gh.getId(), level, code, message, freeHeap, deviceUptime),
                () -> System.err.printf("[LOG] Dropping log from unknown device IP %s%n", ip));
    }

    @Transactional
    public void recordDeviceLog(Long greenhouseId, String level, String code, String message,
                                Integer freeHeap, Long deviceUptime) {
        DeviceLog log = new DeviceLog();
        log.setGreenhouseId(greenhouseId);
        log.setLevel(truncate(level == null || level.isBlank() ? "INFO" : level, 8));
        log.setCode(truncate(code, 64));
        log.setMessage(truncate(message, 512));
        log.setFreeHeap(freeHeap);
        log.setDeviceUptime(deviceUptime);
        log.setReceivedAt(LocalDateTime.now());
        deviceLogRepo.save(log);
    }

    /**
     * Returns the newest {@code limit} log lines for the serial-monitor view,
     * merging device logs with BOOT/CRASH lifecycle events into one timeline.
     */
    @Transactional(readOnly = true)
    public List<DeviceLogDTO> getDeviceLogs(Long greenhouseId, LocalDateTime since, int limit) {
        if (limit <= 0) limit = 200;
        Pageable page = PageRequest.of(0, limit);

        List<DeviceLogDTO> merged = new ArrayList<>();

        deviceLogRepo
                .findByGreenhouseIdAndReceivedAtAfterOrderByReceivedAtDesc(greenhouseId, since, page)
                .forEach(d -> merged.add(new DeviceLogDTO(
                        d.getId(), "DEVICE", d.getLevel(), d.getCode(), d.getMessage(),
                        d.getFreeHeap(), d.getDeviceUptime(), d.getReceivedAt())));

        eventRepo
                .findByGreenhouseIdAndOccurredAtBetweenOrderByOccurredAtDesc(
                        greenhouseId, since, LocalDateTime.now())
                .forEach(e -> merged.add(new DeviceLogDTO(
                        e.getId(), "EVENT",
                        e.getEventType() == GreenhouseEventType.CRASH ? "ERROR" : "INFO",
                        e.getEventType().name(), e.getDetails(),
                        null, null, e.getOccurredAt())));

        merged.sort((a, b) -> b.timestamp().compareTo(a.timestamp()));
        return merged.size() > limit ? merged.subList(0, limit) : merged;
    }

    /**
     * Enforces device-log retention: deletes rows older than {@code retentionDays}
     * (when > 0) and trims each greenhouse to at most {@code maxPerGreenhouse} rows.
     * Called by the cleanup scheduler.
     */
    @Transactional
    public void cleanupDeviceLogs(int retentionDays, int maxPerGreenhouse, LocalDateTime now) {
        if (retentionDays > 0) {
            int deleted = deviceLogRepo.deleteByReceivedAtBefore(now.minusDays(retentionDays));
            if (deleted > 0) {
                System.out.printf("[ANALYTICS CLEANUP] Removed %d device-log rows past retention%n", deleted);
            }
        }
        if (maxPerGreenhouse <= 0) return;

        Pageable nth = PageRequest.of(maxPerGreenhouse - 1, 1); // the maxPerGreenhouse-th newest row
        for (var gh : greenhouseRepository.findAll()) {
            Long id = gh.getId();
            if (deviceLogRepo.countByGreenhouseId(id) <= maxPerGreenhouse) continue;
            List<DeviceLog> boundary = deviceLogRepo.findByGreenhouseIdOrderByReceivedAtDesc(id, nth);
            if (boundary.isEmpty()) continue;
            LocalDateTime cutoff = boundary.get(0).getReceivedAt();
            int trimmed = deviceLogRepo.deleteByGreenhouseIdAndReceivedAtBefore(id, cutoff);
            if (trimmed > 0) {
                System.out.printf("[ANALYTICS CLEANUP] Trimmed %d device-log rows over cap for greenhouse %d%n",
                        trimmed, id);
            }
        }
    }

    private static String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }

    // ─── Parameter history recording ─────────────────────────────────────────────

    /**
     * Records a parameter history snapshot for every parameter that appears
     * in the telemetry message. Must be called inside an active transaction
     * so that lazy-loaded zones/flowerpots can be accessed.
     */
    @Transactional
    public void recordTelemetryReadings(Greenhouse gh, TelemetryGreenhouseDTO telemetry) {
        if (!isEnabled()) return;
        LocalDateTime now = LocalDateTime.now();
        List<ParameterHistoryEntry> entries = new ArrayList<>();

        // Greenhouse-level parameters
        if (telemetry.parameters != null) {
            for (TelemetryParameterDTO pDto : telemetry.parameters) {
                if (pDto.val == null) continue;
                gh.getParameters().stream()
                        .filter(p -> p.getId().equals(pDto.id))
                        .findFirst()
                        .ifPresent(p -> entries.add(buildEntry(p, gh.getId(), pDto.val, now)));
            }
        }

        if (telemetry.zones == null) {
            if (!entries.isEmpty()) historyRepo.saveAll(entries);
            return;
        }

        for (TelemetryZoneDTO zDto : telemetry.zones) {
            Zone zone = gh.getZones().stream()
                    .filter(z -> z.getId().equals(zDto.id))
                    .findFirst().orElse(null);
            if (zone == null) continue;

            // Zone-level parameters
            if (zDto.parameters != null) {
                for (TelemetryParameterDTO pDto : zDto.parameters) {
                    if (pDto.val == null) continue;
                    zone.getParameters().stream()
                            .filter(p -> p.getId().equals(pDto.id))
                            .findFirst()
                            .ifPresent(p -> entries.add(buildEntry(p, gh.getId(), pDto.val, now)));
                }
            }

            // Flowerpot-level parameters
            if (zDto.flowerpots != null) {
                for (TelemetryFlowerpotDTO fpDto : zDto.flowerpots) {
                    Flowerpot fp = zone.getFlowerpots().stream()
                            .filter(f -> f.getId().equals(fpDto.id))
                            .findFirst().orElse(null);
                    if (fp == null || fpDto.parameters == null) continue;

                    for (TelemetryParameterDTO pDto : fpDto.parameters) {
                        if (pDto.val == null) continue;
                        fp.getParameters().stream()
                                .filter(p -> p.getId().equals(pDto.id))
                                .findFirst()
                                .ifPresent(p -> entries.add(buildEntry(p, gh.getId(), pDto.val, now)));
                    }
                }
            }
        }

        if (!entries.isEmpty()) {
            historyRepo.saveAll(entries);
        }
    }

    private ParameterHistoryEntry buildEntry(ParameterEntity p, Long greenhouseId,
                                              Double value, LocalDateTime now) {
        ParameterHistoryEntry entry = new ParameterHistoryEntry();
        entry.setParameterId(p.getId());
        entry.setParameterName(p.getName());
        entry.setUnit(p.getUnit());
        entry.setGreenhouseId(greenhouseId);
        entry.setValue(value);
        entry.setRecordedAt(now);
        return entry;
    }

    // ─── Query methods ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<GreenhouseEventDTO> getGreenhouseEvents(Long greenhouseId,
                                                         LocalDateTime from, LocalDateTime to) {
        return eventRepo
                .findByGreenhouseIdAndOccurredAtBetweenOrderByOccurredAtDesc(greenhouseId, from, to)
                .stream()
                .map(e -> new GreenhouseEventDTO(
                        e.getId(),
                        greenhouseId, // use the parameter directly — avoids lazy-loading the Greenhouse proxy
                        e.getEventType().name(),
                        e.getOccurredAt(),
                        e.getDetails()))
                .toList();
    }

    @Transactional(readOnly = true)
    public GreenhouseStatsDTO getGreenhouseStats(Long greenhouseId,
                                                  LocalDateTime from, LocalDateTime to) {
        long boots = eventRepo.countByGreenhouseIdAndEventTypeAndOccurredAtBetween(
                greenhouseId, GreenhouseEventType.BOOT, from, to);
        long crashes = eventRepo.countByGreenhouseIdAndEventTypeAndOccurredAtBetween(
                greenhouseId, GreenhouseEventType.CRASH, from, to);
        return new GreenhouseStatsDTO(greenhouseId, boots, crashes, from, to);
    }

    /**
     * Returns up to {@code maxPoints} history points for a parameter,
     * uniformly downsampled from the full set when there are more than maxPoints entries.
     */
    @Transactional(readOnly = true)
    public ParameterHistoryResponseDTO getParameterHistory(Long parameterId,
                                                            LocalDateTime from, LocalDateTime to,
                                                            int maxPoints) {
        List<ParameterHistoryEntry> raw = historyRepo
                .findByParameterIdAndRecordedAtBetweenOrderByRecordedAtAsc(parameterId, from, to);

        // Retrieve name / unit from first entry (they are denormalised)
        String paramName = raw.isEmpty() ? "Unknown" : raw.get(0).getParameterName();
        String unit      = raw.isEmpty() ? ""        : raw.get(0).getUnit();

        List<ParameterHistoryPointDTO> points = downsample(raw, maxPoints);
        return new ParameterHistoryResponseDTO(parameterId, paramName, unit, points);
    }

    /** Uniform downsampling: keep every (N/maxPoints)-th entry. */
    private List<ParameterHistoryPointDTO> downsample(List<ParameterHistoryEntry> raw, int maxPoints) {
        if (maxPoints <= 0 || raw.size() <= maxPoints) {
            return raw.stream()
                    .map(e -> new ParameterHistoryPointDTO(e.getRecordedAt(), e.getValue()))
                    .toList();
        }
        int step = raw.size() / maxPoints;
        List<ParameterHistoryPointDTO> result = new ArrayList<>(maxPoints + 1);
        for (int i = 0; i < raw.size(); i += step) {
            ParameterHistoryEntry e = raw.get(i);
            result.add(new ParameterHistoryPointDTO(e.getRecordedAt(), e.getValue()));
        }
        // Always include the last point so the chart doesn't look truncated
        ParameterHistoryEntry last = raw.get(raw.size() - 1);
        ParameterHistoryPointDTO lastPoint = new ParameterHistoryPointDTO(last.getRecordedAt(), last.getValue());
        if (!result.isEmpty() && !result.get(result.size() - 1).timestamp().equals(lastPoint.timestamp())) {
            result.add(lastPoint);
        }
        return result;
    }

    // ─── Analytics settings ───────────────────────────────────────────────────────

    public AnalyticsSettingsDTO getSettings() {
        AnalyticsSettings s = getOrCreateSettings();
        return toDTO(s);
    }

    @Transactional
    public AnalyticsSettingsDTO updateSettings(AnalyticsSettingsDTO dto) {
        AnalyticsSettings s = getOrCreateSettings();
        s.setAnalyticsEnabled(dto.analyticsEnabled());
        s.setHistoryRetentionDays(dto.historyRetentionDays());
        s.setEventsRetentionDays(dto.eventsRetentionDays());
        s.setLogsRetentionDays(dto.logsRetentionDays());
        s.setCleanupIntervalHours(dto.cleanupIntervalHours());
        // lastCleanup is managed by the scheduler, never overwritten here
        settingsRepo.save(s);
        return toDTO(s);
    }

    /** Returns the singleton settings row, creating it with defaults if absent. */
    public AnalyticsSettings getOrCreateSettings() {
        return settingsRepo.findById(1L).orElseGet(() -> {
            AnalyticsSettings defaults = new AnalyticsSettings();
            settingsRepo.save(defaults);
            return defaults;
        });
    }

    private AnalyticsSettingsDTO toDTO(AnalyticsSettings s) {
        return new AnalyticsSettingsDTO(
                s.isAnalyticsEnabled(),
                s.getHistoryRetentionDays(),
                s.getEventsRetentionDays(),
                s.getLogsRetentionDays(),
                s.getCleanupIntervalHours(),
                s.getLastCleanup()
        );
    }
}