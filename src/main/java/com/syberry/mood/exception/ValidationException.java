package com.syberry.mood.exception;

/**
 * Thrown to indicate that a validation error occurred while processing a request.
 */
public class ValidationException extends RuntimeException {

  public ValidationException(String message) {
    super(message);
  }
}
