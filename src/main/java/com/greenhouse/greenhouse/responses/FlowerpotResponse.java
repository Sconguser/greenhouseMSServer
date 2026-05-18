package com.greenhouse.greenhouse.responses;

import com.greenhouse.greenhouse.dtos.ParameterDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FlowerpotResponse {
    private final Long id;
    private final String name;
    private final LocalDateTime createdAt;

    private final List<PlantResponse> plants = new ArrayList<>();

    private final List<ParameterDTO> parameters = new ArrayList<>();

    public FlowerpotResponse (Long id, String name, LocalDateTime createdAt, List<PlantResponse> plants,
                              List<ParameterDTO> parameters)
    {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        if (plants != null) {
            this.plants.addAll(plants);
        }
        if (parameters != null) {
            this.parameters.addAll(parameters);
        }
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

    public List<ParameterDTO> getParameters () {
        return parameters;
    }

    public LocalDateTime getCreatedAt () {
        return createdAt;
    }
}
