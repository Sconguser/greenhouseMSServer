package com.greenhouse.greenhouse.models;

public abstract class Requirement<T> {
    String name;

    protected Requirement (String name) {
        this.name = name;
    }

    public abstract T getLowerThreshold ();

    public abstract void setLowerThreshold (T lowerThreshold);

    public abstract T getUpperThreshold ();

    public abstract void setUpperThreshold (T upperThreshold);

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public abstract String getDescription ();
}
