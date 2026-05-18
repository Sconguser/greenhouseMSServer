package com.greenhouse.greenhouse.responses;

import com.greenhouse.greenhouse.dtos.ParameterDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ZoneResponse {
    private final Long id;
    private final String name;
    private final LocalDateTime createdAt;

    private final List<FlowerpotResponse> flowerpots = new ArrayList<>();

    private final List<ParameterDTO> parameters = new ArrayList<>();

    public ZoneResponse (Long id, String name, LocalDateTime createdAt, List<FlowerpotResponse> flowerpots,
                         List<ParameterDTO> parameters)
    {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        if (flowerpots != null) {
            this.flowerpots.addAll(flowerpots);
        }
        if (parameters != null) {
            this.parameters.addAll(parameters);
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

    public List<ParameterDTO> getParameters () {
        return parameters;
    }

    public LocalDateTime getCreatedAt () {
        return createdAt;
    }
}
