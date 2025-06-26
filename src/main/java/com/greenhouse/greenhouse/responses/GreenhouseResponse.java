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
    private final List<ParameterDTO> requestedParameters = new ArrayList<>();
    private final List<ParameterDTO> currentParameters = new ArrayList<>();

    public GreenhouseResponse (String name, String location, String ipAddress, List<ZoneResponse> zones, Long id,
                               List<ParameterDTO> requestedParameters, List<ParameterDTO> currentParameters)
    {
        this.name = name;
        this.location = location;
        this.ipAddress = ipAddress;
        this.id = id;
        if (zones != null) {
            this.zones.addAll(zones);
        }
        if (currentParameters != null) {
            this.currentParameters.addAll(currentParameters);
        }
        if (requestedParameters != null) {
            this.requestedParameters.addAll(requestedParameters);
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

    public List<ParameterDTO> getRequestedParameters () {
        return requestedParameters;
    }

    public List<ParameterDTO> getCurrentParameters () {
        return currentParameters;
    }
}
