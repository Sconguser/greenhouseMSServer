package com.greenhouse.greenhouse.requests;

public class ZoneStatusRequest {
    private Long zoneId;
    private Double temperature;
    private Double humidity;

    public ZoneStatusRequest (Double temperature, Double humidity) {
        this.temperature = temperature;
        this.humidity = humidity;
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


}
