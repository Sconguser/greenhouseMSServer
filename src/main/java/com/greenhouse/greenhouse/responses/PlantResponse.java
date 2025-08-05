package com.greenhouse.greenhouse.responses;

import com.greenhouse.greenhouse.dtos.RequirementDTO;

import java.util.ArrayList;
import java.util.List;

public class PlantResponse {
    private Long id;
    private String name;
    private String description;
    private byte[] imageData;
    private final List<RequirementDTO> requirements = new ArrayList<>();

    public PlantResponse (Long id, String name, String description, byte[] imageData, List<RequirementDTO> requirements)
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageData = imageData;
        if (requirements != null) {
            this.requirements.addAll(requirements);
        }
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImageData () {
        return imageData;
    }

    public void setImageData (byte[] imageData) {
        this.imageData = imageData;
    }

    public List<RequirementDTO> getRequirements () {
        return requirements;
    }

    public void addRequirement (RequirementDTO requirementDTO) {
        this.requirements.add(requirementDTO);
    }
}
