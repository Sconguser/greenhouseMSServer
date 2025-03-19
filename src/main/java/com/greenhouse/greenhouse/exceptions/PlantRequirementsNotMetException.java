package com.greenhouse.greenhouse.exceptions;

public class PlantRequirementsNotMetException extends RuntimeException {
    public PlantRequirementsNotMetException (String message) {
        super(message);
    }
}
