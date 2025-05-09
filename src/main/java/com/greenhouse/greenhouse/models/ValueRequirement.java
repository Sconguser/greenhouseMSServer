package com.greenhouse.greenhouse.models;

public class ValueRequirement extends Requirement<Double> {
    private Double lowerThreshold;
    private Double upperThreshold;
    private String unit;

    public ValueRequirement (String name, Double lowerThreshold, Double upperThreshold, String unit) {
        super(name);
        this.lowerThreshold = lowerThreshold;
        this.upperThreshold = upperThreshold;
        this.unit = unit;
    }

    @Override
    public Double getLowerThreshold () {
        return lowerThreshold;
    }

    @Override
    public void setLowerThreshold (Double lowerThreshold) {
        this.lowerThreshold = lowerThreshold;
    }

    @Override
    public Double getUpperThreshold () {
        return upperThreshold;
    }

    @Override
    public void setUpperThreshold (Double upperThreshold) {
        this.upperThreshold = upperThreshold;
    }

    public String getUnit () {
        return unit;
    }

    public void setUnit (String unit) {
        this.unit = unit;
    }

    @Override
    public String getDescription () {
        return "Needs to be between " + lowerThreshold + " and " + upperThreshold + unit;
    }
}
