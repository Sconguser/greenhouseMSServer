package com.greenhouse.greenhouse.dtos;


import com.greenhouse.greenhouse.models.ParameterType;

public class RequirementDTO {
    private Long id;
    private String name;
    private Double lowerThreshold;
    private Double upperThreshold;
    private String unit;

    private ParameterType type;

//    public Plant getPlant () {
//        return plant;
//    }

//    public void setPlant (Plant plant) {
//        this.plant = plant;
//    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
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
}
