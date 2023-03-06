package com.syberry.mood.user.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.syberry.mood.exception.InvalidArgumentTypeException;
import com.syberry.mood.user.dto.RoleName;
import com.syberry.mood.user.entity.Role;
import com.syberry.mood.user.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RoleConverterUnitTest {

  @InjectMocks
  private RoleConverter roleConverter;
  @Mock
  private RoleRepository roleRepository;

  private Role role = new Role(3L, RoleName.USER);

  @Test
  public void should_SuccessfullyConvertToRoleFromEnum() {
    when(roleRepository.findByRoleNameIfExists(any())).thenReturn(role);
    assertEquals(roleConverter.convertToEntity(RoleName.USER), role);
  }

  @Test
  public void should_SuccessfullyConvertToRoleFromString() {
    when(roleRepository.findByRoleNameIfExists(any())).thenReturn(role);
    assertEquals(roleConverter.convertToEntity("user"), role);
  }

  @Test
  public void should_ThrowError_When_ConvertingInvalidString() {
    assertThrows(InvalidArgumentTypeException.class, () -> roleConverter.convertToEntity(""));
  }
}
