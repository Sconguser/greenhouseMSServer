package com.greenhouse.greenhouse.models;

import jakarta.persistence.*;

@Entity
public class ParameterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private boolean mutable;

    private Double currentValue;

    private Double requestedValue;
    private Double min;
    private Double max;
    private String unit;
    private ParameterType parameterType;
    @ManyToOne
    @JoinColumn(name = "greenhouse_id")
    private Greenhouse greenhouse;
    @ManyToOne
    @JoinColumn(name = "zone_id")
    private Zone zone;

    @ManyToOne
    @JoinColumn(name = "flowerpot_id")
    private Flowerpot flowerpot;

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

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

