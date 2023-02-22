package com.syberry.mood.employee.dto;

import com.syberry.mood.employee.util.Constants;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Data;

/**
 * This class is used to transfer data to update an employee password.
 */
@Data
public class PasswordUpdatingDto {

  @NotBlank
  private String oldPassword;
  @NotBlank
  @Pattern(regexp = Constants.EMPLOYEE_PASSWORD_REGEX,
      message = Constants.EMPLOYEE_PASSWORD_MESSAGE)
  private String newPassword;
}
