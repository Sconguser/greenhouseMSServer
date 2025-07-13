package com.greenhouse.greenhouse.models;

import jakarta.persistence.*;

@Entity
public class RequirementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double lowerThreshold;
    private Double upperThreshold;
    private String unit;
    private ParameterType type;
    @ManyToOne
    @JoinColumn(name = "plant_id")
    private Plant plant;
    
    public Plant getPlant () {
        return this.plant;
    }

    public void setPlant (Plant plant) {
        this.plant = plant;
    }

    public String getName(){
        return name;
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
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
}
