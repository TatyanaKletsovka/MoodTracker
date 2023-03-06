package com.syberry.mood.authorization.service.impl;

import com.syberry.mood.authorization.security.UserDetailsImpl;
import com.syberry.mood.exception.EntityNotFoundException;
import com.syberry.mood.user.entity.User;
import com.syberry.mood.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of user details management service.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  /**
   * Gets user from user repository.
   *
   * @param username user's login
   * @return user's detail information
   * @throws EntityNotFoundException throws when user doesn't exist in repository
   */
  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws EntityNotFoundException {
    User user = userRepository.findUserByUsernameAndDisabledFalseIfExists(username);
    return UserDetailsImpl.create(user);
  }
}
