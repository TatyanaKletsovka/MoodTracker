package com.syberry.mood.employee.dto;

import com.syberry.mood.employee.util.Constants;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class is used to transfer data to create a new employee.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCreatingDto {

  @Email
  @NotBlank
  private String email;
  @NotBlank
  @Pattern(regexp = Constants.EMPLOYEE_PASSWORD_REGEX,
      message = Constants.EMPLOYEE_PASSWORD_MESSAGE)
  private String password;
  @NotBlank
  @Size(max = 50)
  private String firstName;
  @NotBlank
  @Size(max = 50)
  private String lastName;
  @NotBlank
  private String roleName;
}
