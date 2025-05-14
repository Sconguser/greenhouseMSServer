package com.greenhouse.greenhouse.dtos;

import com.greenhouse.greenhouse.models.ToggleValue;

public class ToggleParameterDTO extends ParameterDTO {
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
