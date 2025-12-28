package com.greenhouse.greenhouse.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ParameterType {
    @JsonProperty("Toggle")
    TOGGLE,
    @JsonProperty("Value")
    VALUE
}
