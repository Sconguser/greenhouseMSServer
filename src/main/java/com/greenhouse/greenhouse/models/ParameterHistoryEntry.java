package com.greenhouse.greenhouse.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * One time-series data point for a parameter, written every time
 * a telemetry message arrives from the ESP8266 (~every 10 s).
 *
 * <p>The parameter name and unit are denormalised so that history
 * survives parameter deletion and the query is a simple range scan.</p>
 */
@Entity
@Table(name = "parameter_history",
        indexes = @Index(name = "idx_param_hist_param_time", columnList = "parameter_id, recorded_at"))
public class ParameterHistoryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parameter_id", nullable = false)
    private Long parameterId;

    @Column(name = "parameter_name")
    private String parameterName;

    @Column(name = "unit")
    private String unit;

    @Column(name = "greenhouse_id", nullable = false)
    private Long greenhouseId;

    @Column(nullable = false)
    private Double value;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    public ParameterHistoryEntry() {}

    public Long getId() { return id; }

    public Long getParameterId() { return parameterId; }
    public void setParameterId(Long parameterId) { this.parameterId = parameterId; }

    public String getParameterName() { return parameterName; }
    public void setParameterName(String parameterName) { this.parameterName = parameterName; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Long getGreenhouseId() { return greenhouseId; }
    public void setGreenhouseId(Long greenhouseId) { this.greenhouseId = greenhouseId; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }

    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }
}