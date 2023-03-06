package com.syberry.mood.authorization.security;

import com.syberry.mood.user.entity.User;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Implementation of UserDetails interface which contains id, username, password and roles.
 */
@Getter
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

  private final Long id;
  private final String username;
  private final String password;
  private final GrantedAuthority grantedAuthority;

  /**
   * Presents collection of authorities.
   *
   * @return list of user's roles
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(grantedAuthority);
  }

  /**
   * Return account expiration state.
   *
   * @return true if account wasn't expired and false if was
   */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /**
   * Return account locking state.
   *
   * @return true if account wasn't locked and false if was
   */
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  /**
   * Return credential expiration state.
   *
   * @return true if credential wasn't expired and false if was
   */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /**
   * Return user's state.
   *
   * @return true if user is active and false if isn't
   */
  @Override
  public boolean isEnabled() {
    return true;
  }

  /**
   * Create user details from user.
   *
   * @param user user object
   * @return customized user details
   */
  public static UserDetailsImpl create(User user) {
    return new UserDetailsImpl(user.getId(), user.getUsername(), user.getPassword(),
        new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleName().name()));
  }
}