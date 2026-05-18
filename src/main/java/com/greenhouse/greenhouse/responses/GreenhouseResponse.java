package com.greenhouse.greenhouse.responses;

import com.greenhouse.greenhouse.dtos.ParameterDTO;
import com.greenhouse.greenhouse.models.Status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GreenhouseResponse {

    private final Long id;
    private final String name;
    private final String location;
    private final String ipAddress;
    private final Status status;
    private final LocalDateTime lastUpdate;
    private final LocalDateTime lastPushed;
    private final Boolean deviceConfigSynced;
    private final Boolean mappingConfigSynced;
    private final Boolean modelSynced;
    private final List<ZoneResponse> zones = new ArrayList<>();
    private final List<ParameterDTO> parameters = new ArrayList<>();

    public GreenhouseResponse (String name, String location, String ipAddress, List<ZoneResponse> zones, Long id,
                               List<ParameterDTO> parameters, Status status, LocalDateTime lastUpdate,
                               LocalDateTime lastPushed, Boolean deviceConfigSynced, Boolean mappingConfigSynced,
                               Boolean modelSynced)
    {
        this.name = name;
        this.location = location;
        this.ipAddress = ipAddress;
        this.id = id;
        this.status = status;
        this.lastUpdate = lastUpdate;
        this.lastPushed = lastPushed;
        this.deviceConfigSynced = deviceConfigSynced;
        this.mappingConfigSynced = mappingConfigSynced;
        this.modelSynced = modelSynced;
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

    public Status getStatus () {
        return status;
    }

    public LocalDateTime getLastUpdate () {
        return lastUpdate;
    }

    public LocalDateTime getLastPushed () {
        return lastPushed;
    }

    public Boolean getDeviceConfigSynced () {
        return deviceConfigSynced;
    }

    public Boolean getMappingConfigSynced () {
        return mappingConfigSynced;
    }

    public Boolean getModelSynced () {
        return modelSynced;
    }
}
