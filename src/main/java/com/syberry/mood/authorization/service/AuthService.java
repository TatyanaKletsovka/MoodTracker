package com.syberry.mood.authorization.service;

import com.syberry.mood.authorization.dto.LoginDto;
import com.syberry.mood.authorization.dto.LoginRequestDto;
import javax.servlet.http.HttpServletRequest;

/**
 * User authorization interface.
 */
public interface AuthService {

  /**
   * Allows the user to log into application.
   *
   * @param dto login details
   * @return tokens and logged in user
   */
  LoginDto login(LoginRequestDto dto);

  /**
   * Extends duration of the token.
   *
   * @param request http request
   * @return refreshed tokens
   */
  LoginDto refreshToken(HttpServletRequest request);

  /**
   * Clear login information in system.
   *
   * @return cleared tokens
   */
  LoginDto logout();
}
