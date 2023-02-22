package com.syberry.mood.employee.dto;

import com.syberry.mood.user.dto.RoleName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class is used to transfer data about employee.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDto {

  private Long id;
  private String email;
  private String firstName;
  private String lastName;
  private RoleName roleName;
  private boolean disabled;
}
