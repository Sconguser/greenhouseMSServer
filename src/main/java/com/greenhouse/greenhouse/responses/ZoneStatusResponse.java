package com.greenhouse.greenhouse.responses;

public class ZoneStatusResponse {
    private double temperature;
    private double humidity;

    public ZoneStatusResponse (double temperature, double humidity) {
        this.temperature = temperature;
        this.humidity = humidity;
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

}
