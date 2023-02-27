package com.syberry.mood.authorization.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object that represents request to log in user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

  @NotEmpty
  @Size(max = 50)
  private String username;
  @NotEmpty
  private String password;
}
