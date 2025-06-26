package com.greenhouse.greenhouse.models;

import jakarta.persistence.Entity;

@Entity
public class ValueRequirementEntity extends RequirementEntity {
    Double lowerThreshold;

    Double upperThreshold;
    String unit;

    public Double getLowerThreshold () {
        return lowerThreshold;
    }

    public void setLowerThreshold (Double lowerThreshold) {
        this.lowerThreshold = lowerThreshold;
    }

    public Double getUpperThreshold () {
        return upperThreshold;
    }

    public void setUpperThreshold (Double upperThreshold) {
        this.upperThreshold = upperThreshold;
    }

    public String getUnit () {
        return unit;
    }

    public void setUnit (String unit) {
        this.unit = unit;
    }
}
