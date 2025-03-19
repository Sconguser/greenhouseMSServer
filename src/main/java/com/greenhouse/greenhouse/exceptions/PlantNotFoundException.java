package com.greenhouse.greenhouse.exceptions;

public class PlantNotFoundException extends RuntimeException{
    public PlantNotFoundException(String message){
        super(message);
    }
}
