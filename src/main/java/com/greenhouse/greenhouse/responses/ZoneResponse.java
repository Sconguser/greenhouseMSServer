package com.greenhouse.greenhouse.responses;

import com.greenhouse.greenhouse.dtos.ParameterDTO;

import java.util.ArrayList;
import java.util.List;

public class ZoneResponse {
    private final Long id;
    private final String name;

    private final List<FlowerpotResponse> flowerpots = new ArrayList();

    private final List<ParameterDTO> currentParameters = new ArrayList<>();
    private final List<ParameterDTO> requestedParameters = new ArrayList<>();

    public ZoneResponse (Long id, String name, List<FlowerpotResponse> flowerpots, List<ParameterDTO> currentParameters,
                         List<ParameterDTO> requestedParameters)
    {
        this.id = id;
        this.name = name;
        if (flowerpots != null) {
            this.flowerpots.addAll(flowerpots);
        }
        if (currentParameters != null) {
            this.currentParameters.addAll(currentParameters);
        }
        if (requestedParameters != null) {
            this.requestedParameters.addAll(requestedParameters);
        }

    }

    public Long getId () {
        return id;
    }

    public String getName () {
        return name;
    }

    public List<FlowerpotResponse> getFlowerpots () {
        return flowerpots;
    }

    public List<ParameterDTO> getCurrentParameters () {
        return currentParameters;
    }

    public List<ParameterDTO> getRequestedParameters () {
        return requestedParameters;
    }
}
