package com.syberry.mood.exception;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Provides global exception handling for REST controllers.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends Exception {

  /**
   * Handles custom exceptions.
   * Returns an HTTP Bad Request (400) status code.
   *
   * @param ex the exception to handle
   * @return an HTTP response entity containing the errors
   */
  @ExceptionHandler({
      AccessException.class,
      EntityNotFoundException.class,
      InvalidArgumentTypeException.class,
      TokenRefreshException.class,
      ValidationException.class,
      ConstraintViolationException.class
  })
  public final ResponseEntity<Map<String, List<String>>> customExceptionHandler(Exception ex) {
    List<String> errors = Collections.singletonList(ex.getMessage());
    return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles validation errors when the request body fails validation.
   * Returns an HTTP Bad Request (400) status code.
   *
   * @param ex the exception to handle
   * @return an HTTP response entity containing the validation errors
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, List<Map<String, Object>>>> validationErrorsHandler(
      MethodArgumentNotValidException ex) {
    Map<String, String> error = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
      error.put(fieldError.getField(), fieldError.getDefaultMessage());
    });
    Map<String, Object> response = new HashMap<>();
    response.put("ValidationErrors", error);
    return new ResponseEntity<>(getErrorsMap(response), new HttpHeaders(), HttpStatus.BAD_REQUEST);
  }


  /**
   * Returns a Map of errors to be used in a response entity.
   *
   * @param errors the list of errors to add to the Map
   * @return a Map of errors with the key "errors"
   */
  private Map<String, List<String>> getErrorsMap(List<String> errors) {
    Map<String, List<String>> errorResponse = new HashMap<>();
    errorResponse.put("errors", errors);
    return errorResponse;
  }

  /**
   * Returns a Map of errors to be used in a response entity.
   *
   * @param errors the Map of errors to add to the List in the Map
   * @return a Map of errors with the key "errors"
   */
  private Map<String, List<Map<String, Object>>> getErrorsMap(Map<String, Object> errors) {
    Map<String, List<Map<String, Object>>> errorResponse = new HashMap<>();
    errorResponse.put("errors", List.of(errors));
    return errorResponse;
  }
}
