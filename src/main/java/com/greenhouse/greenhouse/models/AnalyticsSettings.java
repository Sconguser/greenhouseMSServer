package com.greenhouse.greenhouse.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Singleton settings row (id always = 1) for the analytics subsystem.
 *
 * <ul>
 *   <li>{@code analyticsEnabled}     — master switch; when false, no data is recorded.</li>
 *   <li>{@code historyRetentionDays} — delete parameter_history rows older than this.</li>
 *   <li>{@code eventsRetentionDays}  — delete greenhouse_event rows older than this.</li>
 *   <li>{@code cleanupIntervalHours} — how often the cleanup job should fire.</li>
 *   <li>{@code lastCleanup}          — timestamp of the most recent successful cleanup run.</li>
 * </ul>
 */
@Entity
@Table(name = "analytics_settings")
public class AnalyticsSettings {

    /** Always 1 — singleton row. */
    @Id
    private Long id = 1L;

    /** Master switch. When {@code false} no readings or events are recorded. */
    @Column(name = "analytics_enabled", nullable = false)
    private boolean analyticsEnabled = true;

    @Column(name = "history_retention_days", nullable = false)
    private int historyRetentionDays = 90;

    @Column(name = "events_retention_days", nullable = false)
    private int eventsRetentionDays = 365;

    /** Delete device_log rows older than this. Logs are noisy, so the default is short. */
    @Column(name = "logs_retention_days", nullable = false)
    private int logsRetentionDays = 7;

    /**
     * How often (in hours) the cleanup job should delete stale data.
     * The @Scheduled checker fires every hour; it uses this value plus
     * {@code lastCleanup} to decide whether to actually run.
     */
    @Column(name = "cleanup_interval_hours", nullable = false)
    private int cleanupIntervalHours = 24;

    @Column(name = "last_cleanup")
    private LocalDateTime lastCleanup;

    public AnalyticsSettings() {}

    public Long getId() { return id; }

    public boolean isAnalyticsEnabled() { return analyticsEnabled; }
    public void setAnalyticsEnabled(boolean analyticsEnabled) {
        this.analyticsEnabled = analyticsEnabled;
    }

    public int getHistoryRetentionDays() { return historyRetentionDays; }
    public void setHistoryRetentionDays(int historyRetentionDays) {
        this.historyRetentionDays = historyRetentionDays;
    }

    public int getEventsRetentionDays() { return eventsRetentionDays; }
    public void setEventsRetentionDays(int eventsRetentionDays) {
        this.eventsRetentionDays = eventsRetentionDays;
    }

    public int getLogsRetentionDays() { return logsRetentionDays; }
    public void setLogsRetentionDays(int logsRetentionDays) {
        this.logsRetentionDays = logsRetentionDays;
    }

    public int getCleanupIntervalHours() { return cleanupIntervalHours; }
    public void setCleanupIntervalHours(int cleanupIntervalHours) {
        this.cleanupIntervalHours = cleanupIntervalHours;
    }

    public LocalDateTime getLastCleanup() { return lastCleanup; }
    public void setLastCleanup(LocalDateTime lastCleanup) { this.lastCleanup = lastCleanup; }
}