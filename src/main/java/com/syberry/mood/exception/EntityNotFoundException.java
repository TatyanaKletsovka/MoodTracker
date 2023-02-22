package com.syberry.mood.exception;

/**
 * Thrown to indicate that an entity is not found.
 */
public class EntityNotFoundException extends RuntimeException {

  public EntityNotFoundException(String message) {
    super(message);
  }
}
