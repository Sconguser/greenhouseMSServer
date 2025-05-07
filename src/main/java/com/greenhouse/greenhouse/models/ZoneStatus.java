package com.greenhouse.greenhouse.models;

import jakarta.persistence.*;

@Entity
public class ZoneStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double temperature;
    private double humidity;

    @OneToOne(mappedBy = "status", cascade = CascadeType.ALL, optional = false)
    private Zone zone;

    public ZoneStatus () {
    }

    public Zone getZone () {
        return zone;
    }

    public void setZone (Zone zone) {
        this.zone = zone;
    }

    public double getHumidity () {
        return humidity;
    }

    public void setHumidity (double humidity) {
        this.humidity = humidity;
    }

    public double getTemperature () {
        return temperature;
    }

    public void setTemperature (double temperature) {
        this.temperature = temperature;
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

}
