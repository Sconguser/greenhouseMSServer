package com.greenhouse.greenhouse.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Greenhouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "greenhouse", cascade = CascadeType.ALL)
    private List<Zone> zones;

    private String name;
    private String location;
    private String ipAddress;

    private Status status = Status.OFF;

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

    public Status getStatus () {
        return status;
    }

    public void setStatus (Status status) {
        this.status = status;
    }
}
