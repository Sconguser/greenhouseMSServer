package com.greenhouse.greenhouse.models;

import jakarta.persistence.Entity;

@Entity
public class ValueParameterEntity extends ParameterEntity {
    double value;
    double min;
    double max;
    String unit;
}
