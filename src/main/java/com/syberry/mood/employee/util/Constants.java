package com.syberry.mood.employee.util;

/**
 * Utility class containing reusable constant fields using for working with employee data.
 */
public class Constants {

  public static final String EMPLOYEE_PASSWORD_REGEX =
      "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9@#$%^&+=]).{7,}$";
  public static final String EMPLOYEE_PASSWORD_MESSAGE =
      "Employee password must be at least 7 characters long,  contain lower, upper,"
          + " not alphabetic symbols.";
}
