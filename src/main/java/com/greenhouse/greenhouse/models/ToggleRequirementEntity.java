package com.greenhouse.greenhouse.models;

import jakarta.persistence.Entity;

@Entity
public class ToggleRequirementEntity extends RequirementEntity{
    ToggleValue lowerThreshold;

    ToggleValue upperThreshold;

    public ToggleValue getUpperThreshold () {
        return upperThreshold;
    }

    public void setUpperThreshold (ToggleValue upperThreshold) {
        this.upperThreshold = upperThreshold;
    }

    public ToggleValue getLowerThreshold () {
        return lowerThreshold;
    }

    public void setLowerThreshold (ToggleValue lowerThreshold) {
        this.lowerThreshold = lowerThreshold;
    }
}
