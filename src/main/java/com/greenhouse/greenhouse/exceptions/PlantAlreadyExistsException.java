package com.greenhouse.greenhouse.exceptions;

public class PlantAlreadyExistsException extends RuntimeException {
    public PlantAlreadyExistsException (String message) {
        super(message);
    }
}
