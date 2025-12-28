package com.greenhouse.greenhouse.dtos.telemetry;

public class DeviceConfigDTO {
    public String name;
    public String driver; // "digital", "dht22", "muxAnalog"
    public String type;   // "value", "toggle"
    public Integer pin;
    public Float minValue;
    public Float maxValue;
}