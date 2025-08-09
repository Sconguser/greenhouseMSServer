package com.greenhouse.greenhouse.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Greenhouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "greenhouse", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Zone> zones = new ArrayList<>();

    private String name;
    private String location;
    private String ipAddress;

    private Status status = Status.OFF;

    @OneToMany(mappedBy = "greenhouse", cascade = CascadeType.ALL)
    @JsonManagedReference("greenhouse-params")
    private final List<ParameterEntity> parameters = new ArrayList<>();

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

    public void addParameter (ParameterEntity parameterEntity) {
        this.parameters.add(parameterEntity);
    }

    public void removeParameter (String name) {
        this.parameters.removeIf(parameterEntity -> parameterEntity.getName()
                .equals(name));
    }

    public List<ParameterEntity> getParameters () {
        return this.parameters;
    }

    public void removeZone (Long zoneId) {
        this.zones.removeIf(zone -> zone.getId()
                .equals(zoneId));
    }

}
