package com.greenhouse.greenhouse.exceptions;

public class GreenhouseNotFoundException extends RuntimeException{
    public GreenhouseNotFoundException(String message){
        super(message);
    }
}
