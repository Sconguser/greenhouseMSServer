package com.greenhouse.greenhouse.dtos;

public class ValueParameterDTO extends ParameterDTO {
    private double value;

    private double min;
    private double max;
    private String unit;

    public double getValue () {
        return value;
    }

    public void setValue (double value) {
        this.value = value;
    }

    public double getMin () {
        return min;
    }

    public void setMin (double min) {
        this.min = min;
    }

    public double getMax () {
        return max;
    }

    public void setMax (double max) {
        this.max = max;
    }

    public String getUnit () {
        return unit;
    }

    public void setUnit (String unit) {
        this.unit = unit;
    }
}
