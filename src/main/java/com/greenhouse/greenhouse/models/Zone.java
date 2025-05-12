package com.greenhouse.greenhouse.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Zone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL)
    private List<Flowerpot> flowerpots;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "status_id", unique = true)
    private ZoneStatus zoneStatus;

    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL)
    private List<ParameterEntity> parameterEntities;

    public Zone () {
    }

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

    public String getDescription () {
        return description;
    }

    public void setDescription (String description) {
        this.description = description;
    }

    public List<Flowerpot> getFlowerpots () {
        return flowerpots;
    }

    public void setFlowerpots (List<Flowerpot> flowerpots) {
        this.flowerpots = flowerpots;
    }

    public ZoneStatus getZoneStatus () {
        return zoneStatus;
    }

    public void setZoneStatus (ZoneStatus zoneStatus) {
        this.zoneStatus = zoneStatus;
    }

    public void addParameterEntity (ParameterEntity parameterEntity) {
        parameterEntity.setZone(this);
        this.parameterEntities.add(parameterEntity);
    }

    public void deleteParameterEntity (String name) {
        this.parameterEntities.removeIf(parameterEntity -> parameterEntity.name.equals(name));
    }

    public List<ParameterEntity> getParameterEntities () {
        return this.parameterEntities;
    }
}
