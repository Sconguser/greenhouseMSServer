package com.greenhouse.greenhouse.dtos.telemetry;

import java.util.List;

public class MappingDTO {
    public String scope;      // "greenhouse", "zone", "flowerpot"
    public Long zoneId;
    public Long flowerpotId;
    public String paramName;

    // Reading
    public String readDriver;
    public Integer readPin;
    public Integer muxChannel;
    public List<Integer> muxSelPins;

    // Writing
    public String writeDriver;
    public Integer writePin;
    public String direction;  // "increase", "decrease"
    public Double hysteresis;
    public Boolean activeLow;
    public Integer minOnMs;
    public Integer minOffMs;
    public String outputMode; // "binary"
}