package com.greenhouse.greenhouse.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
public class ParameterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private boolean mutable;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "requested_value_updated_at")
    private LocalDateTime requestedValueUpdatedAt;

    @JsonIgnore
    public LocalDateTime getCreatedAt () {
        return createdAt;
    }

    @JsonIgnore
    public LocalDateTime getUpdatedAt () {
        return updatedAt;
    }

    @JsonIgnore
    public LocalDateTime getRequestedValueUpdatedAt () {
        return requestedValueUpdatedAt;
    }

    public void setRequestedValueUpdatedAt (LocalDateTime requestedValueUpdatedAt) {
        this.requestedValueUpdatedAt = requestedValueUpdatedAt;
    }

    private Double currentValue;

    private Double requestedValue;
    private Double min;
    private Double max;
    private String unit;
    private ParameterType parameterType;
    @ManyToOne
    @JoinColumn(name = "greenhouse_id")
    @JsonBackReference("greenhouse-params")
    private Greenhouse greenhouse;
    @ManyToOne
    @JoinColumn(name = "zone_id")
    @JsonBackReference("zone-params")
    private Zone zone;

    @ManyToOne
    @JoinColumn(name = "flowerpot_id")
    @JsonBackReference("flowerpot-params")
    private Flowerpot flowerpot;

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    // Arduino reads src["mutable"] — pin the key name explicitly so it survives
    // any future rename of this getter.
    @JsonProperty("mutable")
    public boolean isMutable () {
        return mutable;
    }

    public void setMutable (boolean mutable) {
        this.mutable = mutable;
    }

    public Greenhouse getGreenhouse () {
        return greenhouse;
    }

    public void setGreenhouse (Greenhouse greenhouse) {
        this.greenhouse = greenhouse;
    }

    public Zone getZone () {
        return zone;
    }

    public void setZone (Zone zone) {
        this.zone = zone;
    }

    public Flowerpot getFlowerpot () {
        return flowerpot;
    }

    public void setFlowerpot (Flowerpot flowerpot) {
        this.flowerpot = flowerpot;
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public Double getCurrentValue () {
        return currentValue;
    }

    public void setCurrentValue (Double currentValue) {
        this.currentValue = currentValue;
    }

    public Double getRequestedValue () {
        return requestedValue;
    }

    public void setRequestedValue (Double requestedValue) {
        this.requestedValue = requestedValue;
    }

    public Double getMin () {
        return min;
    }

    public void setMin (Double min) {
        this.min = min;
    }

    public Double getMax () {
        return max;
    }

    public void setMax (Double max) {
        this.max = max;
    }

    public String getUnit () {
        return unit;
    }

    public void setUnit (String unit) {
        this.unit = unit;
    }

    public ParameterType getParameterType () {
        return parameterType;
    }

    public void setParameterType (ParameterType parameterType) {
        this.parameterType = parameterType;
    }
}

