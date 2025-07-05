package com.greenhouse.greenhouse.exceptions;

public class ParameterNotFoundException extends RuntimeException {
  public ParameterNotFoundException (String message) {
    super(message);
  }
}
