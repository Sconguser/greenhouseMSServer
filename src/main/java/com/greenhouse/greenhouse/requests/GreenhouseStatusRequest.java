package com.greenhouse.greenhouse.requests;

import com.greenhouse.greenhouse.models.Status;

public class GreenhouseStatusRequest {
    private Double temperature;
    private Double humidity;
    private Status status;

    public GreenhouseStatusRequest (Double temperature, Double humidity, Status status) {
        this.temperature = temperature;
        this.humidity = humidity;
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
}
