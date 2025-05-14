package com.greenhouse.greenhouse.models;

import jakarta.persistence.Entity;

@Entity
public class ValueParameterEntity extends ParameterEntity {
    Double currentValue;
    Double requestedValue;
    Double min;
    Double max;
    String unit;
}
