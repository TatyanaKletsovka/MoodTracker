package com.syberry.mood.user.converter;

import com.syberry.mood.user.dto.PatientCreationDto;
import com.syberry.mood.user.dto.PatientDto;
import com.syberry.mood.user.dto.RoleName;
import com.syberry.mood.user.dto.UserDto;
import com.syberry.mood.user.entity.Role;
import com.syberry.mood.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserConverterUnitTest {

  @InjectMocks
  private UserConverter userConverter;
  @Mock
  private RoleConverter roleConverter;

  private User user = new User();
  private PatientCreationDto creationDto = new PatientCreationDto();
  private PatientDto patientDto = new PatientDto();
  private UserDto userDto = new UserDto();
  private Role role = new Role(3L, RoleName.USER);

  @BeforeEach
  public void mock() {
    Long id = 2L;
    String username = "Superhero Name";
    String password = "catdogcat";
    LocalDateTime createdAt = LocalDateTime.now();
    boolean disabled = false;

    user.setId(id);
    user.setUsername(username);
    user.setPassword(password);
    user.setRole(role);
    user.setCreatedAt(createdAt);
    user.setDisabled(disabled);

    creationDto.setSuperheroName(username);
    creationDto.setPassword(password);

    userDto.setId(id);
    userDto.setUsername(username);
    userDto.setRoleName(RoleName.USER);

    patientDto.setId(id);
    patientDto.setSuperheroName(username);
    patientDto.setDisabled(disabled);
  }


  @Test
  public void should_SuccessfullyConvertPatientCreationDtoToEntity() {
    when(roleConverter.convertToEntity(RoleName.USER)).thenReturn(role);
    assertThat(userConverter.convertToEntity(creationDto))
      .usingRecursiveComparison()
      .ignoringFields("id", "createdAt")
      .isEqualTo(user);
  }

  @Test
  public void should_SuccessfullyConvertToUserDto() {
    assertEquals(userConverter.convertToUserDto(user), userDto);
  }

  @Test
  public void should_SuccessfullyConvertToPatientDto() {
    assertEquals(userConverter.convertToPatientDto(user), patientDto);
  }
}
