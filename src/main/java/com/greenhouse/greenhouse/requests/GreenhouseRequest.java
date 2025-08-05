package com.greenhouse.greenhouse.requests;


import com.greenhouse.greenhouse.dtos.ParameterDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

public class GreenhouseRequest {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Location is required")
    private String location;
    @Pattern(regexp = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$", message = "Invalid IP address")
    private String ipAddress;

    private List<ParameterDTO> parameters = new ArrayList<>();


    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getLocation () {
        return location;
    }

    public void setLocation (String location) {
        this.location = location;
    }

    public String getIpAddress () {
        return ipAddress;
    }

    public void setIpAddress (String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public List<ParameterDTO> getParameters () {
        return parameters;
    }

    public void setParameters (List<ParameterDTO> parameters) {
        this.parameters = parameters;
    }
}
