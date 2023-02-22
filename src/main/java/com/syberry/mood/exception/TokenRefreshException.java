package com.syberry.mood.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown to indicate that an access token refresh operation failed.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class TokenRefreshException extends RuntimeException {

  public TokenRefreshException(String message) {
    super(String.format(message));
  }
}
