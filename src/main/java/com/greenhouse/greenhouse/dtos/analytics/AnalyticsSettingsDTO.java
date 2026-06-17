package com.greenhouse.greenhouse.dtos.analytics;

import java.time.LocalDateTime;

public record AnalyticsSettingsDTO(
        boolean analyticsEnabled,
        int historyRetentionDays,
        int eventsRetentionDays,
        int logsRetentionDays,
        int cleanupIntervalHours,
        LocalDateTime lastCleanup
) {}