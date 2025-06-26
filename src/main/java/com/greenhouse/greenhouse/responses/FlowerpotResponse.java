package com.greenhouse.greenhouse.responses;

import com.greenhouse.greenhouse.dtos.ParameterDTO;

import java.util.ArrayList;
import java.util.List;

public class FlowerpotResponse {
    private final Long id;

    private final String name;

    private final List<PlantResponse> plants = new ArrayList<>();

    private final List<ParameterDTO> currentParameters = new ArrayList<>();

    private final List<ParameterDTO> requestedParameters = new ArrayList<>();

    public FlowerpotResponse (Long id, String name, List<PlantResponse> plants, List<ParameterDTO> currentParameters,
                              List<ParameterDTO> requestedParameters)
    {
        this.id = id;
        this.name = name;
        if (plants != null) {
            this.plants.addAll(plants);
        }
        if (currentParameters != null) {
            this.currentParameters.addAll(currentParameters);
        }
        if (requestedParameters != null) {
            this.requestedParameters.addAll(requestedParameters);
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

    public List<ParameterDTO> getCurrentParameters () {
        return currentParameters;
    }

    public List<ParameterDTO> getRequestedParameters () {
        return requestedParameters;
    }
}
