package com.greenhouse.greenhouse.models;

import jakarta.persistence.*;

@Entity
public class GreenhouseStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double temperature;
    private double humidity;

    private double soilHumidity;

    private Status status = Status.OFF;
    @OneToOne(mappedBy = "status", cascade = CascadeType.ALL, optional = false)
    private Greenhouse greenhouse;
    public GreenhouseStatus () {
    }

    public Greenhouse getGreenhouse () {
        return greenhouse;
    }

    public void setGreenhouse (Greenhouse greenhouse) {
        this.greenhouse = greenhouse;
    }

    public Status getStatus () {
        return status;
    }

    public void setStatus (Status status) {
        this.status = status;
    }

    public double getTemperature () {
        return temperature;
    }

    public void setTemperature (double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity () {
        return humidity;
    }

    public void setHumidity (double humidity) {
        this.humidity = humidity;
    }

    public double getSoilHumidity () {
        return soilHumidity;
    }

    public void setSoilHumidity (double soilHumidity) {
        this.soilHumidity = soilHumidity;
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    @Override
    public String toString () {
        return "Current Greenhouse status: \n" + "temperature: " + this.temperature + "\n" + "humidity: " + this.humidity + "\n" + "status: " + this.status.name() + "\n";
    }
}
