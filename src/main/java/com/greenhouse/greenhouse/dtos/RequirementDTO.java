package com.greenhouse.greenhouse.dtos;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.greenhouse.greenhouse.models.Plant;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({@JsonSubTypes.Type(value = ValueRequirementDTO.class, name = "VALUE"), @JsonSubTypes.Type(value = ToggleParameterDTO.class, name = "TOGGLE")})
public class RequirementDTO {
    private Long id;
    private String name;

    private Plant plant;

    public Plant getPlant () {
        return plant;
    }

    public void setPlant (Plant plant) {
        this.plant = plant;
    }

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
}
