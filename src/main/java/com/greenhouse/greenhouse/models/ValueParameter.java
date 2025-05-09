package com.greenhouse.greenhouse.models;

import com.greenhouse.greenhouse.exceptions.ParameterNotMutableException;

public class ValueParameter extends Parameter<Double> {
    private Double value;

    private Double min;

    private Double max;
    private String unit;

    public ValueParameter (String name, Double value, Double min, Double max, String unit) {
        super(name);
        this.value = value;
        this.unit = unit;
        this.min = min;
        this.max = max;
    }

    @Override
    public Double getValue () {
        return value;
    }

    @Override
    public void setValue (Double value) {
        if (!isMutable()) {
            throw new ParameterNotMutableException("Parameter is read only");
        }
        if (value < min || value > max) {
            throw new IllegalArgumentException("Value outside of bounds");
        }
        this.value = value;
    }

    @Override
    public String getDescription () {
        return value.toString() + unit;
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
}
