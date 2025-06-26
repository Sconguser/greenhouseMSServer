package com.greenhouse.greenhouse.models;

import jakarta.persistence.Entity;

@Entity
public class ToggleParameterEntity extends ParameterEntity {
    private ToggleValue currentValue;

    private ToggleValue requestedValue;

    public ToggleValue getCurrentValue () {
        return currentValue;
    }

    public void setCurrentValue (ToggleValue currentValue) {
        this.currentValue = currentValue;
    }

    public ToggleValue getRequestedValue () {
        return requestedValue;
    }

    public void setRequestedValue (ToggleValue requestedValue) {
        this.requestedValue = requestedValue;
    }
}
