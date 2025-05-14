package com.greenhouse.greenhouse.dtos;

public class ValueRequirementDTO extends RequirementDTO{
    private Double lowerThreshold;
    private Double upperThreshold;

    private String unit;

    public String getUnit () {
        return unit;
    }

    public void setUnit (String unit) {
        this.unit = unit;
    }

    public Double getUpperThreshold () {
        return upperThreshold;
    }

    public void setUpperThreshold (Double upperThreshold) {
        this.upperThreshold = upperThreshold;
    }

    public Double getLowerThreshold () {
        return lowerThreshold;
    }

    public void setLowerThreshold (Double lowerThreshold) {
        this.lowerThreshold = lowerThreshold;
    }
}
