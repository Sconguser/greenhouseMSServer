package com.greenhouse.greenhouse.models;


public class Parameter {
    private String name;
    private boolean mutable = true;

    private Double currentValue;

    private Double requestedValue;
    private Double min;
    private Double max;
    private String unit;
    private ParameterType parameterType;
    protected Parameter (String name) {
        this.name = name;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public boolean isMutable () {
        return mutable;
    }

    protected void setMutable (boolean mutable) {
        this.mutable = mutable;
    }

    public Double getCurrentValue () {
        return currentValue;
    }

    public void setCurrentValue (Double currentValue) {
        this.currentValue = currentValue;
    }

    public Double getRequestedValue () {
        return requestedValue;
    }

    public void setRequestedValue (Double requestedValue) {
        this.requestedValue = requestedValue;
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

    public ParameterType getParameterType () {
        return parameterType;
    }

    public void setParameterType (ParameterType parameterType) {
        this.parameterType = parameterType;
    }

}
