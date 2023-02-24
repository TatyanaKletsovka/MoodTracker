package com.syberry.mood.user.converter;

import com.syberry.mood.user.dto.PatientCreationDto;
import com.syberry.mood.user.dto.PatientDto;
import com.syberry.mood.user.dto.RoleName;
import com.syberry.mood.user.dto.UserDto;
import com.syberry.mood.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * A component that provides converting methods for the User entity.
 */
@Component
@RequiredArgsConstructor
public class UserConverter {

  private final RoleConverter converter;

  /**
   * Converts a PatientCreationDto object to a User entity.
   *
   * @param dto the PatientCreationDto object to be converted.
   * @return the User entity corresponding to the given dto.
   */
  public User convertToEntity(PatientCreationDto dto) {
    return User.builder()
        .username(dto.getSuperheroName())
        .password(dto.getPassword())
        .role(converter.convertToEntity(RoleName.USER))
        .build();
  }

  /**
   * Converts a User entity to a UserDto object.
   *
   * @param user the User entity to be converted.
   * @return the UserDto object corresponding to the given user.
   */
  public UserDto convertToUserDto(User user) {
    return UserDto.builder()
        .id(user.getId())
        .username(user.getUsername())
        .roleName(user.getRole().getRoleName())
        .build();
  }

  /**
   * Converts a User entity to a PatientDto object.
   *
   * @param user the User entity to be converted.
   * @return the PatientDto object corresponding to the given user.
   */
  public PatientDto convertToPatientDto(User user) {
    return PatientDto.builder()
        .id(user.getId())
        .superheroName(user.getUsername())
        .disabled(user.isDisabled())
        .build();
  }
}
