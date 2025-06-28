package com.greenhouse.greenhouse.models;

import jakarta.persistence.Entity;

@Entity
public class ValueParameterEntity extends ParameterEntity {
    private Double currentValue;
    private Double requestedValue;
    private Double min;
    private Double max;
    private String unit;

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
}
