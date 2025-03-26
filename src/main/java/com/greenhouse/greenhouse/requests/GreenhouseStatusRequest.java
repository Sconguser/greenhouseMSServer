package com.greenhouse.greenhouse.requests;

import com.greenhouse.greenhouse.models.Status;

public class GreenhouseStatusRequest {
    private Double temperature;
    private Double humidity;
    private Double soilHumidity;
    private Status status;

    public GreenhouseStatusRequest (Double temperature, Double humidity, Double soilHumidity, Status status) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.soilHumidity = soilHumidity;
        this.status = status;
    }

    public Double getTemperature () {
        return temperature;
    }

    public void setTemperature (double temperature) {
        this.temperature = temperature;
    }

    public Double getHumidity () {
        return humidity;
    }

    public void setHumidity (double humidity) {
        this.humidity = humidity;
    }


    public Status getStatus () {
        return status;
    }

    public void setStatus (Status status) {
        this.status = status;
    }

    public Double getSoilHumidity () {
        return soilHumidity;
    }

    public void setSoilHumidity (Double soilHumidity) {
        this.soilHumidity = soilHumidity;
    }
}
