package com.syberry.mood.exception;

/**
 * Thrown to indicate that an error occurred while sending an email.
 */
public class EmailException extends RuntimeException {

  public EmailException(Throwable throwable, String message) {
    super(message, throwable);
  }
}
