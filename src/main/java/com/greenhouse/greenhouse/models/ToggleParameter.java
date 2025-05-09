package com.greenhouse.greenhouse.models;


import com.greenhouse.greenhouse.exceptions.ParameterNotMutableException;

public class ToggleParameter extends Parameter<ToggleValue> {
    private ToggleValue value;

    public ToggleParameter (String name, ToggleValue value) {
        super(name);
        this.value = value;
    }

    @Override
    public ToggleValue getValue () {
        return value;
    }

    @Override
    public void setValue (ToggleValue value) {
        if(!isMutable()){
            throw new ParameterNotMutableException("Parameter is read only");
        }
        this.value = value;
    }

    @Override
    public String getDescription () {
        return value.name();
    }
}
