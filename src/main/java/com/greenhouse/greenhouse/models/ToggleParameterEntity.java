package com.greenhouse.greenhouse.models;

import jakarta.persistence.Entity;

@Entity
public class ToggleParameterEntity extends ParameterEntity {
    ToggleValue currentValue;
    ToggleValue requestedValue;
}
