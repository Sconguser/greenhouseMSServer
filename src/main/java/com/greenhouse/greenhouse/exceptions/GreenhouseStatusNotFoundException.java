package com.greenhouse.greenhouse.exceptions;

public class GreenhouseStatusNotFoundException extends RuntimeException{
    public GreenhouseStatusNotFoundException(String message){
        super(message);
    }
}
