package com.greenhouse.greenhouse.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * One plant-health breach: a plant's requirement that the live conditions are
 * (or were) violating. Tracks a small state machine — PENDING (breach started),
 * ACTIVE (breach sustained past the debounce window → user notified), RESOLVED
 * (conditions back in range). At most one non-RESOLVED row exists per
 * (plant, flowerpot, requirement).
 */
@Entity
@Table(name = "plant_alert",
        indexes = {
            @Index(name = "idx_plant_alert_gh_status", columnList = "greenhouse_id, status"),
            @Index(name = "idx_plant_alert_key", columnList = "plant_id, flowerpot_id, requirement_name, status")
        })
public class PlantAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "greenhouse_id", nullable = false)
    private Long greenhouseId;
    private String greenhouseName;

    @Column(name = "flowerpot_id")
    private Long flowerpotId;
    private String flowerpotName;

    @Column(name = "plant_id")
    private Long plantId;
    private String plantName;

    @Column(name = "requirement_name")
    private String requirementName;
    private String parameterName;
    private String unit;

    @Enumerated(EnumType.STRING)
    private RequirementKind kind;

    private Double lowerThreshold;
    private Double upperThreshold;
    /** Most recent out-of-range reading seen for this breach. */
    private Double lastValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlantAlertStatus status;

    @Column(length = 512)
    private String message;

    private LocalDateTime firstDetectedAt;
    private LocalDateTime raisedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime lastSeenAt;

    public PlantAlert() {}

    public Long getId() { return id; }

    public Long getGreenhouseId() { return greenhouseId; }
    public void setGreenhouseId(Long greenhouseId) { this.greenhouseId = greenhouseId; }

    public String getGreenhouseName() { return greenhouseName; }
    public void setGreenhouseName(String greenhouseName) { this.greenhouseName = greenhouseName; }

    public Long getFlowerpotId() { return flowerpotId; }
    public void setFlowerpotId(Long flowerpotId) { this.flowerpotId = flowerpotId; }

    public String getFlowerpotName() { return flowerpotName; }
    public void setFlowerpotName(String flowerpotName) { this.flowerpotName = flowerpotName; }

    public Long getPlantId() { return plantId; }
    public void setPlantId(Long plantId) { this.plantId = plantId; }

    public String getPlantName() { return plantName; }
    public void setPlantName(String plantName) { this.plantName = plantName; }

    public String getRequirementName() { return requirementName; }
    public void setRequirementName(String requirementName) { this.requirementName = requirementName; }

    public String getParameterName() { return parameterName; }
    public void setParameterName(String parameterName) { this.parameterName = parameterName; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public RequirementKind getKind() { return kind; }
    public void setKind(RequirementKind kind) { this.kind = kind; }

    public Double getLowerThreshold() { return lowerThreshold; }
    public void setLowerThreshold(Double lowerThreshold) { this.lowerThreshold = lowerThreshold; }

    public Double getUpperThreshold() { return upperThreshold; }
    public void setUpperThreshold(Double upperThreshold) { this.upperThreshold = upperThreshold; }

    public Double getLastValue() { return lastValue; }
    public void setLastValue(Double lastValue) { this.lastValue = lastValue; }

    public PlantAlertStatus getStatus() { return status; }
    public void setStatus(PlantAlertStatus status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getFirstDetectedAt() { return firstDetectedAt; }
    public void setFirstDetectedAt(LocalDateTime firstDetectedAt) { this.firstDetectedAt = firstDetectedAt; }

    public LocalDateTime getRaisedAt() { return raisedAt; }
    public void setRaisedAt(LocalDateTime raisedAt) { this.raisedAt = raisedAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public LocalDateTime getLastSeenAt() { return lastSeenAt; }
    public void setLastSeenAt(LocalDateTime lastSeenAt) { this.lastSeenAt = lastSeenAt; }
}
