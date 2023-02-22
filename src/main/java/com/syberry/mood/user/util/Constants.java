package com.syberry.mood.user.util;

/**
 * Utility class containing reusable constant fields for the application.
 */
public class Constants {

  public static final String SUPERHERO_NAME_REGEX = "^[A-Z][a-z]+\\s[A-Z][a-z]+$";
  public static final String SUPERHERO_NAME_MESSAGE =
      "The superheroName must consist of two capital words separated by a space";
  public static final String PATIENT_PASSWORD_REGEX = "^[a-zA-Z]+_[a-zA-Z]+_[a-zA-Z]+$";
  public static final String PATIENT_PASSWORD_MESSAGE =
      "The password must consist of 3 words separated by a '_' character";
}
