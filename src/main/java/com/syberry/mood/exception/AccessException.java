package com.syberry.mood.exception;

/**
 * Thrown to indicate that a user tries to perform an action for which they do not have permission.
 */
public class AccessException extends RuntimeException {

  public AccessException(String message) {
    super(message);
  }
}
