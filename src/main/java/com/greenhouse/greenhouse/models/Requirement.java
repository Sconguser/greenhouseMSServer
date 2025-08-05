package com.greenhouse.greenhouse.models;

public class Requirement {
    private String name;
    private Double lowerThreshold;
    private Double upperThreshold;
    private String unit;
    private ParameterType type;
//    private Plant plant;


    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

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

    public ParameterType getType () {
        return type;
    }

    public void setType (ParameterType type) {
        this.type = type;
    }

//    public Plant getPlant () {
//        return plant;
//    }
//
//    public void setPlant (Plant plant) {
//        this.plant = plant;
//    }
}
