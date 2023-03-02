package com.syberry.mood.employee.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class is used to transfer data to update an employee.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeUpdatingDto {

  private Long id;
  @Email
  @NotBlank
  private String email;
  @NotBlank
  @Size(max = 50)
  private String firstName;
  @NotBlank
  @Size(max = 50)
  private String lastName;
  @NotBlank
  private String roleName;
}
