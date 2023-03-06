package com.syberry.mood.authorization.repository;

import com.syberry.mood.authorization.entity.RefreshToken;
import com.syberry.mood.exception.TokenRefreshException;
import com.syberry.mood.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing and storing refresh tokens.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  /**
   * Finds refresh token by its data.
   *
   * @param token refresh token
   * @return an Optional containing refresh token if it exists, or an empty Optional if id does not
   */
  Optional<RefreshToken> findByToken(String token);

  /**
   * Finds refresh token by user.
   *
   * @param user user entity
   * @return an Optional containing refresh token if it exists, or an empty Optional if id does not
   */
  Optional<RefreshToken> findByUser(User user);

  /**
   * Delete refresh token by user entity id.
   *
   * @param id user entity id
   */
  void deleteByUserId(Long id);

  /**
   * Finds a token by its data and throws an TokenRefreshException if it does not exist.
   *
   * @param token token's data
   * @return refresh token
   */
  default RefreshToken findRefreshTokenByTokenIfExists(String token) {
    return findByToken(token)
        .orElseThrow(() -> new TokenRefreshException("Refresh token doesn't exist"));
  }

  /**
   * Finds a token by user and throws an TokenRefreshException if it does not exist.
   *
   * @param user owner
   * @return refresh token
   */
  default RefreshToken findRefreshTokenByUserIfExists(User user) {
    return findByUser(user)
        .orElseThrow(() -> new TokenRefreshException("Refresh token doesn't exist"));
  }
}
