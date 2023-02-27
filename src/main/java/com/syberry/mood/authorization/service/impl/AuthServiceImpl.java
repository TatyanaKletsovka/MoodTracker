package com.syberry.mood.authorization.service.impl;

import com.syberry.mood.authorization.dto.LoginDto;
import com.syberry.mood.authorization.dto.LoginRequestDto;
import com.syberry.mood.authorization.entity.RefreshToken;
import com.syberry.mood.authorization.security.UserDetailsImpl;
import com.syberry.mood.authorization.service.AuthService;
import com.syberry.mood.authorization.service.RefreshTokenService;
import com.syberry.mood.authorization.util.SecurityUtils;
import com.syberry.mood.exception.TokenRefreshException;
import com.syberry.mood.user.converter.UserConverter;
import com.syberry.mood.user.dto.UserDto;
import com.syberry.mood.user.repository.UserRepository;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Implementation of AuthService interface.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final AuthenticationManager authenticationManager;
  private final RefreshTokenService refreshTokenService;
  private final UserRepository userRepository;
  private final UserConverter userConverter;
  private final SecurityUtils securityUtils;

  /**
   * Allows the user to log into application.
   *
   * @param dto login details
   * @return tokens and logged in user
   */
  @Override
  public LoginDto login(LoginRequestDto dto) {
    UserDto userDto = findUserByUsername(dto.getUsername());
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(dto.getUsername(),
            dto.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    return authenticateUser((UserDetailsImpl) authentication.getPrincipal(), userDto);
  }

  /**
   * Extends duration of the token.
   *
   * @param request http request
   * @return refreshed tokens
   */
  @Override
  public LoginDto refreshToken(HttpServletRequest request) {
    String refreshToken = securityUtils.getJwtRefreshFromCookies(request);
    if (refreshToken != null && !refreshToken.isEmpty()) {
      return refreshTokenService.refreshAccessToken(refreshToken);
    } else {
      throw new TokenRefreshException("Refresh token is empty");
    }
  }

  /**
   * Clear login information in system.
   *
   * @return cleared tokens
   */
  @Override
  public LoginDto logout() {
    Long userId = SecurityUtils.getUserDetails().getId();
    refreshTokenService.deleteByUserId(userId);
    ResponseCookie accessCookie = securityUtils.getCleanJwtCookie();
    ResponseCookie refreshCookie = securityUtils.getCleanJwtRefreshCookie();
    return LoginDto.builder()
        .cookie(accessCookie.toString())
        .refreshCookie(refreshCookie.toString())
        .build();
  }

  /**
   * Authenticate user.
   *
   * @param userDetails user's details data
   * @return cookies with tokens
   */
  private LoginDto authenticateUser(UserDetailsImpl userDetails, UserDto userDto) {
    ResponseCookie responseCookie = securityUtils.generateJwtCookie(userDetails);
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
    ResponseCookie refreshJwtCookie = securityUtils.generateRefreshJwtCookie(
        refreshToken.getToken());
    return LoginDto.builder()
        .cookie(responseCookie.toString())
        .refreshCookie(refreshJwtCookie.toString())
        .userDto(userDto)
        .build();
  }

  /**
   * Sends a request to get the active user by name to user repository.
   *
   * @param username username
   * @return userDto if user exist in repository
   */
  private UserDto findUserByUsername(String username) {
    return userConverter.convertToUserDto(
        userRepository.findUserByUsernameAndDisabledFalseIfExists(username));
  }
}
