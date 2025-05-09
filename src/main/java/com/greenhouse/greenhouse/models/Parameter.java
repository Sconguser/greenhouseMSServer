package com.greenhouse.greenhouse.models;


public abstract class Parameter<T> {
    private String name;
    private boolean mutable = true;

    protected Parameter (String name) {
        this.name = name;
    }

    public abstract T getValue ();

    public abstract void setValue (T value);

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
