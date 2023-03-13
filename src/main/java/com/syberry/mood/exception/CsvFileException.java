package com.syberry.mood.exception;

/**
 * Thrown to indicate that file creation failed.
 */
public class CsvFileException extends RuntimeException {

  public CsvFileException(String message, Throwable cause) {
    super(message, cause);
  }
}
