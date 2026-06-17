package com.greenhouse.greenhouse.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * A single free-text log line emitted by an ESP8266 device over MQTT
 * (topic {@code greenhouse/{ip}/log}). High-volume and short-lived — kept
 * separate from {@link GreenhouseEvent} (low-volume BOOT/CRASH markers used
 * by analytics charts) so the two never pollute each other's queries.
 *
 * <p>Ordering and retention use {@code receivedAt} (stamped by the server on
 * arrival) rather than the device's {@code deviceUptime}, because the board
 * has no wall clock and reboots daily.</p>
 */
@Entity
@Table(name = "device_log",
        indexes = @Index(name = "idx_device_log_gh_time", columnList = "greenhouse_id, received_at"))
public class DeviceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "greenhouse_id", nullable = false)
    private Long greenhouseId;

    /** "INFO" | "WARN" | "ERROR". */
    @Column(nullable = false, length = 8)
    private String level;

    /** Short stable tag, e.g. "model.parse". */
    @Column(length = 64)
    private String code;

    @Column(length = 512)
    private String message;

    /** Free heap (bytes) reported by the device when the log was emitted. */
    @Column(name = "free_heap")
    private Integer freeHeap;

    /** Device uptime in seconds at emission — debugging aid, not used for ordering. */
    @Column(name = "device_uptime")
    private Long deviceUptime;

    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;

    public DeviceLog() {}

    public Long getId() { return id; }

    public Long getGreenhouseId() { return greenhouseId; }
    public void setGreenhouseId(Long greenhouseId) { this.greenhouseId = greenhouseId; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Integer getFreeHeap() { return freeHeap; }
    public void setFreeHeap(Integer freeHeap) { this.freeHeap = freeHeap; }

    public Long getDeviceUptime() { return deviceUptime; }
    public void setDeviceUptime(Long deviceUptime) { this.deviceUptime = deviceUptime; }

    public LocalDateTime getReceivedAt() { return receivedAt; }
    public void setReceivedAt(LocalDateTime receivedAt) { this.receivedAt = receivedAt; }
}
