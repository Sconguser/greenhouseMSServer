package com.greenhouse.greenhouse.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Records a significant lifecycle event for a greenhouse device:
 * either a BOOT (came back online) or a CRASH (went unexpectedly offline).
 */
@Entity
@Table(name = "greenhouse_event",
        indexes = @Index(name = "idx_gh_event_gh_time", columnList = "greenhouse_id, occurred_at"))
public class GreenhouseEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "greenhouse_id", nullable = false)
    private Greenhouse greenhouse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GreenhouseEventType eventType;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    /** Optional free-text note (e.g. "was NOT_RESPONSIVE → BOOT"). */
    private String details;

    public GreenhouseEvent() {}

    public GreenhouseEvent(Greenhouse greenhouse, GreenhouseEventType eventType,
                           LocalDateTime occurredAt, String details) {
        this.greenhouse = greenhouse;
        this.eventType = eventType;
        this.occurredAt = occurredAt;
        this.details = details;
    }

    public Long getId() { return id; }
    public Greenhouse getGreenhouse() { return greenhouse; }
    public GreenhouseEventType getEventType() { return eventType; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public String getDetails() { return details; }
}