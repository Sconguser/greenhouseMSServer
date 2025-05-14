package com.greenhouse.greenhouse.dtos;

public class ValueParameterDTO extends ParameterDTO {
    private Double currentValue;
    private Double requestedValue;
    private Double min;
    private Double max;
    private String unit;

    public Double getCurrentValue () {
        return currentValue;
    }

    public void setCurrentValue (Double currentValue) {
        this.currentValue = currentValue;
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
        this.requestedValue = requestedValue;
    }
}
