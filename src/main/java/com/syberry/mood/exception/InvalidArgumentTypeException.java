package com.syberry.mood.exception;

/**
 * Thrown to indicate that an invalid argument type was provided for a method or operation.
 */
public class InvalidArgumentTypeException extends RuntimeException {

  public InvalidArgumentTypeException(String message) {
    super(message);
  }
}
