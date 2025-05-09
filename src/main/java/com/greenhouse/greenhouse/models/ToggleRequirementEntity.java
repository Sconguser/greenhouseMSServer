package com.greenhouse.greenhouse.models;

import jakarta.persistence.Entity;

@Entity
public class ToggleRequirementEntity extends RequirementEntity{
    ToggleValue lowerThreshold;
    ToggleValue upperThreshold;

}
