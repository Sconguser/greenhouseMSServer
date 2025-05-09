package com.greenhouse.greenhouse.models;

public class ToggleRequirement extends Requirement<ToggleValue> {
    private ToggleValue lowerThreshold;
    private ToggleValue upperThreshold;

    public ToggleRequirement (String name, ToggleValue lowerThreshold, ToggleValue upperThreshold) {
        super(name);
        this.lowerThreshold = lowerThreshold;
        this.upperThreshold = upperThreshold;
    }

    @Override
    public ToggleValue getLowerThreshold () {
        return lowerThreshold;
    }

    @Override
    public void setLowerThreshold (ToggleValue lowerThreshold) {
        this.lowerThreshold = lowerThreshold;
    }

    @Override
    public ToggleValue getUpperThreshold () {
        return upperThreshold;
    }

    @Override
    public void setUpperThreshold (ToggleValue upperThreshold) {
        this.upperThreshold = upperThreshold;
    }

    @Override
    public String getDescription () {
        if (lowerThreshold == upperThreshold) {
            return "Needs to be " + lowerThreshold.name();
        } else {
            return "Need to be " + lowerThreshold.name() + " or " + upperThreshold.name();
        }
    }
}
