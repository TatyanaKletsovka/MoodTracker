package com.syberry.mood.authorization.controller;

import com.syberry.mood.authorization.dto.LoginDto;
import com.syberry.mood.authorization.dto.LoginRequestDto;
import com.syberry.mood.authorization.service.AuthService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller for handling authentication and authorization-related HTTP requests.
 */
@RestController
@CrossOrigin
@Validated
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  private final AuthService authService;

  /**
   * Handles the user's authentication request.
   *
   * @param dto login details
   * @return tokens and logged in user
   */
  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto dto) {
    log.info("POST-request: log in");
    return createHeader(authService.login(dto));
  }

  /**
   * Handles the user's log out request.
   *
   * @return cleared tokens
   */
  @PostMapping("/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR', 'USER')")
  public ResponseEntity<?> logout() {
    log.info("POST-request: log out");
    return createHeader(authService.logout());
  }

  /**
   * Handles the user's refresh token request.
   *
   * @param httpServletRequest http request data
   * @return refreshed tokens
   */
  @PostMapping("/refresh")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<?> refreshToken(HttpServletRequest httpServletRequest) {
    log.info("POST-request: refresh token");
    return createHeader(authService.refreshToken(httpServletRequest));
  }

  /**
   * Creates http header for response.
   *
   * @param loginDto login details
   * @return created http header
   */
  private ResponseEntity<?> createHeader(LoginDto loginDto) {
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, loginDto.getCookie())
        .header(HttpHeaders.SET_COOKIE, loginDto.getRefreshCookie())
        .body(loginDto.getUserDto());
  }
}
