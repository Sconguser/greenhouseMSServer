package com.greenhouse.greenhouse.responses;

import java.util.ArrayList;
import java.util.List;

public class GreenhouseResponse {
    private final String name;
    private final String location;
    private final String ipAddress;
    private final List<PlantResponse> plantResponses = new ArrayList<>();
    private final GreenhouseStatusResponse greenhouseStatusResponse;

    public GreenhouseResponse (String name, String location, String ipAddress,
                               GreenhouseStatusResponse greenhouseStatusResponse, List<PlantResponse> plantResponses)
    {
        this.name = name;
        this.location = location;
        this.ipAddress = ipAddress;
        this.greenhouseStatusResponse = greenhouseStatusResponse;
        if (plantResponses == null) {
            plantResponses = new ArrayList<>();
        }
        this.plantResponses.addAll(plantResponses);
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

    public List<PlantResponse> getPlantResponses () {
        return plantResponses;
    }

    public GreenhouseStatusResponse getGreenhouseStatusResponse () {
        return greenhouseStatusResponse;
    }
}
