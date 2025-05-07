package com.greenhouse.greenhouse.responses;

import java.util.ArrayList;
import java.util.List;

public class GreenhouseResponse {


    private final Long id;
    private final String name;
    private final String location;
    private final String ipAddress;
    private final List<PlantResponse> plants = new ArrayList<>();
    private final ZoneStatusResponse status;

    public GreenhouseResponse (String name, String location, String ipAddress,
                               ZoneStatusResponse status, List<PlantResponse> plants,
                               Long id)
    {
        this.name = name;
        this.location = location;
        this.ipAddress = ipAddress;
        this.status = status;
        this.id = id;
        if (plants == null) {
            plants = new ArrayList<>();
        }
        this.plants.addAll(plants);
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

    public List<PlantResponse> getPlants () {
        return plants;
    }

    public ZoneStatusResponse getStatus () {
        return status;
    }

    public Long getId () {
        return id;
    }
}
