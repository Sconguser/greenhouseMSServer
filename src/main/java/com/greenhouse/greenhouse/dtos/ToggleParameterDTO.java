package com.greenhouse.greenhouse.dtos;

import com.greenhouse.greenhouse.models.ToggleValue;

public class ToggleParameterDTO extends ParameterDTO {
    private ToggleValue value;

    public ToggleValue getValue () {
        return value;
    }

    public void setValue (ToggleValue value) {
        this.value = value;
    }
}
