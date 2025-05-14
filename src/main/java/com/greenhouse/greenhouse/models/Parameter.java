package com.greenhouse.greenhouse.models;


public abstract class Parameter<T> {
    private String name;
    private boolean mutable = true;

    protected Parameter (String name) {
        this.name = name;
    }

    public abstract T getCurrentValue ();

    public abstract void setCurrentValue (T currentValue);
    public abstract T getRequestedValue();
    public abstract void setRequestedValue(T requestedValue);

    public abstract String getDescription ();

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

}
