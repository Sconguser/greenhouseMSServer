package com.greenhouse.greenhouse.models;


import com.greenhouse.greenhouse.exceptions.ParameterNotMutableException;

public class ToggleParameter extends Parameter<ToggleValue> {
    private ToggleValue currentValue;
    private ToggleValue requestedValue;

    public ToggleParameter (String name, ToggleValue currentValue, ToggleValue requestedValue) {
        super(name);
        this.currentValue = currentValue;
        this.requestedValue = requestedValue;
    }

    @Override
    public ToggleValue getCurrentValue () {
        return currentValue;
    }

    @Override
    public void setCurrentValue (ToggleValue currentValue) {
        this.currentValue = currentValue;
    }

    @Override
    public ToggleValue getRequestedValue () {
        return requestedValue;
    }

    @Override
    public void setRequestedValue (ToggleValue requestedValue) {
        if(!isMutable()){
            throw new ParameterNotMutableException("Parameter is read only");
        }
        this.requestedValue = requestedValue;
    }

    @Override
    public String getDescription () {
        return currentValue.name();
    }
}
