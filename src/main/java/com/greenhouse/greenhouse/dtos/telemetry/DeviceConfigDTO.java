package com.greenhouse.greenhouse.dtos.telemetry;

public class DeviceConfigDTO {
    public Integer id;    // server-assigned stable id; mappings reference this
    public String name;
    public String driver; // "digital", "dht22", "muxAnalog"
    public String type;   // "value", "toggle"
    public Integer pin;
}