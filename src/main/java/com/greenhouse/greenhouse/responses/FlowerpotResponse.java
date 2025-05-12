package com.greenhouse.greenhouse.responses;

import java.util.ArrayList;
import java.util.List;

public class FlowerpotResponse {
    private final Long id;

    private final String name;

    private final List<PlantResponse> plants = new ArrayList<>();

    public FlowerpotResponse (Long id, String name, List<PlantResponse> plants) {
        this.id = id;
        this.name = name;
        if (plants == null) {
            plants = new ArrayList<>();
        }
        this.plants.addAll(plants);
    }

    public List<PlantResponse> getPlants () {
        return plants;
    }

    public String getName () {
        return name;
    }

    public Long getId () {
        return id;
    }
}
