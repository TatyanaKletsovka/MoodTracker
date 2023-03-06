package com.syberry.mood.authorization.service.impl;

import com.syberry.mood.authorization.dto.LoginDto;
import com.syberry.mood.authorization.entity.RefreshToken;
import com.syberry.mood.authorization.repository.RefreshTokenRepository;
import com.syberry.mood.authorization.service.RefreshTokenService;
import com.syberry.mood.authorization.util.SecurityUtils;
import com.syberry.mood.exception.TokenRefreshException;
import com.syberry.mood.user.entity.User;
import com.syberry.mood.user.repository.UserRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of token management service.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

  private static final long REFRESH_TOKEN_EXPIRATION_TIME = 1;

  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;
  private final SecurityUtils securityUtils;

  /**
   * Refreshes user's access tokens.
   *
   * @param accessToken user's access token
   * @return refreshed tokens
   */
  @Override
  public LoginDto refreshAccessToken(String accessToken) {
    User user = verifyExpiration(accessToken).getUser();
    ResponseCookie accessCookie = securityUtils.generateJwtCookie(user);
    ResponseCookie refreshCookie = securityUtils.generateRefreshJwtCookie(
        createRefreshToken(user.getId()).getToken());
    return LoginDto.builder()
        .cookie(accessCookie.toString())
        .refreshCookie(refreshCookie.toString())
        .userDto(null)
        .build();
  }

  /**
   * Creates refresh token.
   *
   * @param userId user's id
   * @return refresh token
   */
  @Override
  @Transactional
  public RefreshToken createRefreshToken(Long userId) {
    User user = userRepository.findUserByIdIfExists(userId);
    RefreshToken refreshToken;
    if (refreshTokenRepository.findByUser(user).isPresent()) {
      refreshToken = refreshTokenRepository.findRefreshTokenByUserIfExists(user);
    } else {
      refreshToken = new RefreshToken();
      refreshToken.setUser(user);
    }
    refreshToken.setExpiryDate(Instant.now().plus(REFRESH_TOKEN_EXPIRATION_TIME, ChronoUnit.DAYS));
    refreshToken.setToken(UUID.randomUUID().toString());
    refreshTokenRepository.save(refreshToken);
    return refreshToken;
  }

  /**
   * Checks if the token has expired.
   *
   * @param token user's token
   * @return refresh token
   */
  @Override
  public RefreshToken verifyExpiration(String token) {
    RefreshToken refreshToken = refreshTokenRepository.findRefreshTokenByTokenIfExists(token);
    if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
      refreshTokenRepository.delete(refreshToken);
      throw new TokenRefreshException("Session time was expired. Please log in again");
    }
    return refreshToken;
  }

  /**
   * Deletes refresh token from repository.
   *
   * @param userId user's id
   */
  @Override
  @Transactional
  public void deleteByUserId(Long userId) {
    User user = userRepository.findUserByIdIfExists(userId);
    refreshTokenRepository.deleteByUserId(user.getId());
  }
}
