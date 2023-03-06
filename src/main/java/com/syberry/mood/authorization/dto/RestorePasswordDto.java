package com.syberry.mood.authorization.dto;

import com.syberry.mood.employee.util.Constants;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This object contains information for resetting a forgotten password and setting a new password.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestorePasswordDto {
  @Email
  @NotBlank
  private String email;
  @NotBlank
  private String token;
  @NotBlank
  @Pattern(regexp = Constants.EMPLOYEE_PASSWORD_REGEX,
      message = Constants.EMPLOYEE_PASSWORD_MESSAGE)
  private String newPassword;
}
