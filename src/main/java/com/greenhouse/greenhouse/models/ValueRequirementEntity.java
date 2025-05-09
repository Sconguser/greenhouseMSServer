package com.greenhouse.greenhouse.models;

import jakarta.persistence.Entity;

@Entity
public class ValueRequirementEntity extends RequirementEntity {
    Double lowerThreshold;
    Double upperThreshold;
    String unit;
}
