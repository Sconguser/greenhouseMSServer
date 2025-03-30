package com.greenhouse.greenhouse.controllers;

import com.greenhouse.greenhouse.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PlantNotFoundException.class)
    public ResponseEntity<ErrorMessage> handlePlantNotFoundException (PlantNotFoundException plantNotFoundException,
                                                                      WebRequest request)
    {
        return composeResponse(plantNotFoundException, request, HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(NotificationNotSentException.class)
    public ResponseEntity<ErrorMessage> handleNotificationNotSentException (
            NotificationNotSentException notificationNotSentException, WebRequest request)
    {
        return composeResponse(notificationNotSentException, request, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(PlantAlreadyExistsException.class)
    public ResponseEntity<ErrorMessage> handlePlantAlreadyExistsException (
            PlantAlreadyExistsException plantAlreadyExistsException, WebRequest request)
    {
        return composeResponse(plantAlreadyExistsException, request, HttpStatus.CONFLICT);
    }

    @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
    @ExceptionHandler(GreenhouseStatusNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleGreenhouseStatusNotFoundException (
            GreenhouseStatusNotFoundException greenhouseStatusNotFoundException, WebRequest request)
    {
        return composeResponse(greenhouseStatusNotFoundException, request, HttpStatus.I_AM_A_TEAPOT);
    }

    private ResponseEntity<ErrorMessage> composeResponse (Exception ex, WebRequest request, HttpStatus status) {
        ErrorMessage message = new ErrorMessage(status.value(), new Date(), ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<ErrorMessage>(message, status);
    }

}
