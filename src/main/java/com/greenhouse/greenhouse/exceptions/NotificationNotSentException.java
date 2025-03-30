package com.greenhouse.greenhouse.exceptions;

public class NotificationNotSentException extends RuntimeException {
    public NotificationNotSentException (String message) {
        super(message);
    }
}
