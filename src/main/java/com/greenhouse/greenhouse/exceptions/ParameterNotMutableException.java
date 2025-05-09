package com.greenhouse.greenhouse.exceptions;

public class ParameterNotMutableException extends RuntimeException {
    public ParameterNotMutableException (String message) {
        super(message);
    }
}
