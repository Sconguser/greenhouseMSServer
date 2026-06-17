package com.greenhouse.greenhouse.tasks;

import com.greenhouse.greenhouse.models.AnalyticsSettings;
import com.greenhouse.greenhouse.repositories.AnalyticsSettingsRepository;
import com.greenhouse.greenhouse.repositories.GreenhouseEventRepository;
import com.greenhouse.greenhouse.repositories.ParameterHistoryRepository;
import com.greenhouse.greenhouse.services.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Periodically deletes old analytics data according to user-configured
 * retention settings.
 *
 * <p>The {@code @Scheduled} check fires every hour. The actual delete only
 * runs when {@code cleanupIntervalHours} have elapsed since the last run
 * (stored as {@code lastCleanup} in the settings row). This lets the user
 * change the cleanup interval at runtime via the REST API without a server
 * restart.</p>
 */
@Component
public class AnalyticsCleanupScheduler {

    @Autowired
    private ParameterHistoryRepository historyRepo;

    @Autowired
    private GreenhouseEventRepository eventRepo;

    @Autowired
    private AnalyticsSettingsRepository settingsRepo;

    @Autowired
    private AnalyticsService analyticsService;

    /** Hard ceiling on stored log rows per greenhouse, independent of time retention. */
    private static final int MAX_LOGS_PER_GREENHOUSE = 1000;

    /** Check every hour whether cleanup is due. */
    @Scheduled(fixedRate = 3_600_000)
    @Transactional
    public void maybeCleanup() {
        AnalyticsSettings settings = settingsRepo.findById(1L)
                .orElse(new AnalyticsSettings()); // use defaults if not yet persisted

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = settings.getLastCleanup() == null
                ? now  // first ever run: go immediately
                : settings.getLastCleanup().plusHours(settings.getCleanupIntervalHours());

        if (now.isBefore(nextRun)) return; // not yet time

        runCleanup(settings, now);
    }

    private void runCleanup(AnalyticsSettings settings, LocalDateTime now) {
        // Parameter history
        if (settings.getHistoryRetentionDays() > 0) {
            LocalDateTime historyCutoff = now.minusDays(settings.getHistoryRetentionDays());
            int deleted = historyRepo.deleteByRecordedAtBefore(historyCutoff);
            System.out.printf("[ANALYTICS CLEANUP] Removed %d parameter history entries older than %s%n",
                    deleted, historyCutoff);
        }

        // Greenhouse events
        if (settings.getEventsRetentionDays() > 0) {
            LocalDateTime eventsCutoff = now.minusDays(settings.getEventsRetentionDays());
            int deleted = eventRepo.deleteByOccurredAtBefore(eventsCutoff);
            System.out.printf("[ANALYTICS CLEANUP] Removed %d event entries older than %s%n",
                    deleted, eventsCutoff);
        }

        // Device logs: time retention + per-greenhouse row cap
        analyticsService.cleanupDeviceLogs(
                settings.getLogsRetentionDays(), MAX_LOGS_PER_GREENHOUSE, now);

        // Persist the updated last-run timestamp
        settings.setLastCleanup(now);
        settingsRepo.save(settings);
    }
}