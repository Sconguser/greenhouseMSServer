package com.greenhouse.greenhouse.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Greenhouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "greenhouse", cascade = CascadeType.ALL)
    private List<Zone> zones = new ArrayList<>();

    private String name;
    private String location;
    private String ipAddress;

    private Status status = Status.OFF;

    @OneToMany(mappedBy = "greenhouse", cascade = CascadeType.ALL)
    private List<ParameterEntity> requestedParameters = new ArrayList<>();

    @OneToMany(mappedBy = "greenhouse", cascade = CascadeType.ALL)
    private List<ParameterEntity> currentParameters = new ArrayList<>();

    public Greenhouse () {
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

    public String getLocation () {
        return location;
    }

    public void setLocation (String location) {
        this.location = location;
    }

    public String getIpAddress () {
        return ipAddress;
    }

    public void setIpAddress (String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String toString () {
        return "Greenhouse: " + this.name + "\n" + "location " + this.location + "\n";
    }

    public List<Zone> getZones () {
        return zones;
    }

    public void setZones (List<Zone> zones) {
        this.zones = zones;
    }

    public void addZone (Zone zone) {
        this.zones.add(zone);
    }

    public Status getStatus () {
        return status;
    }

    public void setStatus (Status status) {
        this.status = status;
    }

    public void addRequestedParameter (ParameterEntity parameterEntity) {
        parameterEntity.setGreenhouse(this);
        this.requestedParameters.add(parameterEntity);
    }

    public void removeRequestedParameter (String name) {
        this.requestedParameters.removeIf(parameterEntity -> parameterEntity.name.equals(name));
    }

    public List<ParameterEntity> getRequestedParameters () {
        return this.requestedParameters;
    }

    public void addCurrentParameter (ParameterEntity currentParameter) {
        currentParameter.setGreenhouse(this);
        this.currentParameters.add(currentParameter);
    }

    public void deleteCurrentParameter (String name) {
        this.currentParameters.removeIf(parameterEntity -> parameterEntity.name.equals(name));
    }

    public List<ParameterEntity> getCurrentParameters () {
        return this.currentParameters;
    }

    public void setCurrentParameters (List<ParameterEntity> currentParameters) {
        this.currentParameters = currentParameters;
    }
}
