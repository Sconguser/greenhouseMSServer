package com.greenhouse.greenhouse.dtos;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.greenhouse.greenhouse.models.Flowerpot;
import com.greenhouse.greenhouse.models.Greenhouse;
import com.greenhouse.greenhouse.models.Zone;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({@JsonSubTypes.Type(value = ValueParameterDTO.class, name = "VALUE"), @JsonSubTypes.Type(value = ToggleParameterDTO.class, name = "TOGGLE"),})
public class ParameterDTO {
    private Long id;

    private String name;
    private boolean mutable;

    private String type;

    private Greenhouse greenhouse;
    private Zone zone;
    private Flowerpot flowerpot;
    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

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

    public String getType () {
        return type;
    }

    public void setType (String type) {
        this.type = type;
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

}
