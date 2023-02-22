package com.syberry.mood.user.converter;

import com.syberry.mood.exception.InvalidArgumentTypeException;
import com.syberry.mood.user.dto.RoleName;
import com.syberry.mood.user.entity.Role;
import com.syberry.mood.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * A component that provides converting methods for the Role entity.
 */
@Component
@RequiredArgsConstructor
public class RoleConverter {

  private final RoleRepository repository;

  /**
   * Converts a RoleName object to a Role entity from repository.
   *
   * @param roleName the RoleName object to be converted.
   * @return the Role entity corresponding to the given roleName.
   */
  public Role convertToEntity(RoleName roleName) {
    return repository.findByRoleNameIfExists(roleName);
  }

  /**
   * Converts a role name string to a Role entity from repository.
   *
   * @param role the role name string to be converted.
   * @return the Role entity corresponding to the given role name.
   * @throws InvalidArgumentTypeException if the given role name is invalid.
   */
  public Role convertToEntity(String role) {
    try {
      return convertToEntity(RoleName.valueOf(role.toUpperCase()));
    } catch (IllegalArgumentException e) {
      throw new InvalidArgumentTypeException(
          String.format("Error while converting invalid role: %s. Valid roles: %s",
              role, RoleName.getNames()));
    }
  }
}
