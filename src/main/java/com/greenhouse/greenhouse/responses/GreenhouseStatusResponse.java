package com.greenhouse.greenhouse.responses;

import com.greenhouse.greenhouse.models.Status;

public class GreenhouseStatusResponse {
    private double temperature;
    private double humidity;
    private Status status;


    public GreenhouseStatusResponse (double temperature, double humidity, Status status) {
        this.temperature = temperature;
        this.humidity = humidity;
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

    public Status getStatus () {
        return status;
    }

    public void setStatus (Status status) {
        this.status = status;
    }
}
