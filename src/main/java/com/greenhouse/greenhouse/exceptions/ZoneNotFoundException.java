package com.greenhouse.greenhouse.exceptions;

public class ZoneNotFoundException extends RuntimeException {
    public ZoneNotFoundException (String message) {
        super(message);
    }
}
