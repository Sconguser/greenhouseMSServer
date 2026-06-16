package com.greenhouse.greenhouse.dtos.telemetry;

import java.util.List;

public class MappingDTO {
    public String scope;      // "greenhouse", "zone", "flowerpot"
    public Long zoneId;
    public Long flowerpotId;
    public String paramName;

    // Reading — references a device in the inventory by its server-assigned id;
    // the board resolves the driver + pin from that device.
    public Integer readDeviceId;
    public Integer muxChannel;
    public List<Integer> muxSelPins;

    // Writing — references a device in the inventory by its id.
    public Integer writeDeviceId;
    public String direction;  // "increase", "decrease"
    public Double hysteresis;
    public Boolean activeLow;
    public Integer minOnMs;
    public Integer minOffMs;
    public String outputMode; // "binary"

    // Analog scaling (raw → physical unit)
    public Float mapInMin;
    public Float mapInMax;
    public Float mapOutMin;
    public Float mapOutMax;
}