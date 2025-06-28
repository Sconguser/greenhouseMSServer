package com.greenhouse.greenhouse.responses;

import com.greenhouse.greenhouse.dtos.ParameterDTO;

import java.util.ArrayList;
import java.util.List;

public class GreenhouseResponse {

    private final Long id;
    private final String name;
    private final String location;
    private final String ipAddress;
    private final List<ZoneResponse> zones = new ArrayList<>();
    private final List<ParameterDTO> parameters = new ArrayList<>();

    public GreenhouseResponse (String name, String location, String ipAddress, List<ZoneResponse> zones, Long id,
                               List<ParameterDTO> parameters)
    {
        this.name = name;
        this.location = location;
        this.ipAddress = ipAddress;
        this.id = id;
        if (zones != null) {
            this.zones.addAll(zones);
        }

        if (parameters != null) {
            this.parameters.addAll(parameters);
        }
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

    public List<ParameterDTO> getParameters () {
        return parameters;
    }

}
