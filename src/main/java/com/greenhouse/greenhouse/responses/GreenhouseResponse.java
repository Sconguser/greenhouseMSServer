package com.greenhouse.greenhouse.responses;

import java.util.ArrayList;
import java.util.List;

public class GreenhouseResponse {

    private final Long id;
    private final String name;
    private final String location;
    private final String ipAddress;
    private final List<ZoneResponse> zones = new ArrayList<>();

    public GreenhouseResponse (String name, String location, String ipAddress, List<ZoneResponse> zones,
                               Long id)
    {
        this.name = name;
        this.location = location;
        this.ipAddress = ipAddress;
        this.id = id;
        if (zones == null) {
            zones = new ArrayList<>();
        }
        this.zones.addAll(zones);
    }

    public String getName () {
        return name;
    }

    public String getLocation () {
        return location;
    }

    public String getIpAddress () {
        return ipAddress;
    }

    public List<ZoneResponse> getZones () {
        return zones;
    }

    public Long getId () {
        return id;
    }
}
