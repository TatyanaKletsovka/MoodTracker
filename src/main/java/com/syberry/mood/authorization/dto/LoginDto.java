package com.syberry.mood.authorization.dto;

import com.syberry.mood.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object that transfer cookies and user information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {

  private String cookie;
  private String refreshCookie;
  private UserDto userDto;
}
