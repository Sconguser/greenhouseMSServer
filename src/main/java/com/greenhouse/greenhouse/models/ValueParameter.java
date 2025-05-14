package com.greenhouse.greenhouse.models;

import com.greenhouse.greenhouse.exceptions.ParameterNotMutableException;

public class ValueParameter extends Parameter<Double> {
    private Double currentValue;
    private Double requestedValue;
    private Double min;
    private Double max;
    private String unit;

    public ValueParameter (String name, Double currentValue, Double requestedValue, Double min, Double max, String unit)
    {
        super(name);
        this.currentValue = currentValue;
        this.requestedValue = requestedValue;
        this.unit = unit;
        this.min = min;
        this.max = max;
    }

    @Override
    public Double getCurrentValue () {
        return currentValue;
    }

    @Override
    public void setCurrentValue (Double currentValue) {
        this.currentValue = currentValue;
    }

    @Override
    public String getDescription () {
        return currentValue.toString() + unit;
    }

    public Double getMin () {
        return min;
    }

    public void setMin (Double min) {
        this.min = min;
    }

    public Double getMax () {
        return max;
    }

    public void setMax (Double max) {
        this.max = max;
    }

    public String getUnit () {
        return unit;
    }

    public void setUnit (String unit) {
        this.unit = unit;
    }

    public Double getRequestedValue () {
        return requestedValue;
    }

    public void setRequestedValue (Double requestedValue) {
        if (!isMutable()) {
            throw new ParameterNotMutableException("Parameter is read only");
        }
        if (requestedValue < min || requestedValue > max) {
            throw new IllegalArgumentException("Value outside of bounds");
        }
        this.requestedValue = requestedValue;
    }
}
