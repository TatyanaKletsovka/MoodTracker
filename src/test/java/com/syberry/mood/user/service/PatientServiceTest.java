package com.syberry.mood.user.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.syberry.mood.authorization.security.UserDetailsImpl;
import com.syberry.mood.exception.EntityNotFoundException;
import com.syberry.mood.exception.ValidationException;
import com.syberry.mood.user.converter.UserConverter;
import com.syberry.mood.user.dto.PatientCreationDto;
import com.syberry.mood.user.dto.PatientDto;
import com.syberry.mood.user.dto.RoleName;
import com.syberry.mood.user.entity.Role;
import com.syberry.mood.user.entity.User;
import com.syberry.mood.user.repository.UserRepository;
import com.syberry.mood.user.service.impl.PatientServiceImpl;
import com.syberry.mood.user.validation.PatientValidator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PatientServiceTest {

  @InjectMocks
  private PatientServiceImpl patientService;
  @Mock
  private UserConverter userConverter;
  @Mock
  private UserRepository userRepository;
  @Mock
  private PatientValidator patientValidator;
  @Mock
  private SecurityContext securityContext;
  @Mock
  private Authentication authentication;
  @Mock
  private PasswordEncoder passwordEncoder;

  private User user = new User();
  private PatientDto patientDto = new PatientDto();
  private PatientCreationDto creationDto = new PatientCreationDto();
  private Role role = new Role(3L, RoleName.USER);

  @BeforeEach
  public void mock() {
    Long id = 2L;
    String username = "Superhero Name";
    String password = "cat_dog_cat";
    LocalDateTime createdAt = LocalDateTime.now();
    boolean disabled = false;

    user.setId(id);
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(password));
    user.setRole(role);
    user.setCreatedAt(createdAt);
    user.setDisabled(disabled);

    patientDto.setId(id);
    patientDto.setSuperheroName(username);
    patientDto.setDisabled(disabled);

    creationDto.setSuperheroName(username);
    creationDto.setPassword(password);

    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(authentication.getPrincipal())
        .thenReturn(new UserDetailsImpl(1L, "doc@gmail.com", "AbSdA_21sSA",
                new SimpleGrantedAuthority(RoleName.ADMIN.name())));
  }

  @Test
  public void should_SuccessfullyReturnAllPatients() {
    when(userRepository.findAllPatientsSortIdDesc()).thenReturn(new ArrayList<>());
    assertEquals(patientService.findAllPatients(), new ArrayList<>());
  }

  @Test
  public void should_SuccessfullyFindPatientById() {
    when(userRepository.findPatientByIdIfExists(any())).thenReturn(user);
    when(userConverter.convertToPatientDto(any())).thenReturn(patientDto);
    assertEquals(patientService.findPatientById(any()), patientDto);
  }

  @Test
  public void should_ThrowError_WhenFindingByIdNoneExistingPatient() {
    when(userRepository.findPatientByIdIfExists(-1L)).thenThrow(EntityNotFoundException.class);
    assertThrows(EntityNotFoundException.class, () -> patientService.findPatientById(-1L));
  }

  @Test
  public void should_SuccessfullyFindPatientProfile() {
    when(userRepository.findPatientByIdIfExists(any())).thenReturn(user);
    when(userConverter.convertToPatientDto(any())).thenReturn(patientDto);
    assertEquals(patientService.findPatientProfile(), patientDto);
  }

  @Test
  public void should_SuccessfullyCreatePatient() {
    when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
    when(userConverter.convertToEntity(any(PatientCreationDto.class))).thenReturn(user);
    when(userRepository.save(any())).thenReturn(user);
    when(userConverter.convertToPatientDto(any())).thenReturn(patientDto);
    assertEquals(patientService.createPatient(creationDto), patientDto);
  }

  @Test
  public void should_ThrowError_When_CreatingPatientWithExistingUsername() {
    when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
    doThrow(ValidationException.class).when(patientValidator).validateSuperheroName(any(), any());
    assertThrows(ValidationException.class, () -> patientService.createPatient(creationDto));
  }

  @Test
  public void should_SuccessfullyUpdatePatientHeroNameById() {
    when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
    when(userRepository.findPatientByIdIfExists(any())).thenReturn(user);
    when(userConverter.convertToPatientDto(any())).thenReturn(patientDto);
    assertEquals(patientService.updatePatientHeroNameById(2L, "Superhero Name"),
        patientDto);
  }

  @Test
  public void should_SuccessfullyUpdatePatientWithExistingInThisPatientHeroNameById() {
    when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
    when(userRepository.findPatientByIdIfExists(any())).thenReturn(user);
    when(userConverter.convertToPatientDto(any())).thenReturn(patientDto);
    assertEquals(patientService.updatePatientHeroNameById(2L, "Superhero Name"),
        patientDto);
  }

  @Test
  public void should_ThrowError_When_UpdatingPatientWithExistingHeroName() {
    doThrow(ValidationException.class).when(patientValidator).validateSuperheroName(any(), any());
    assertThrows(
        ValidationException.class,
        () -> patientService.updatePatientHeroNameById(3L, "Superhero Name"));
  }

  @Test
  public void should_ThrowError_When_UpdatingDisabledPatient() {
    when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
    doThrow(ValidationException.class).when(patientValidator).validateUpdating(any());
    assertThrows(
        ValidationException.class,
        () -> patientService.updatePatientHeroNameById(3L, "Superhero Name"));
  }

  @Test
  public void should_SuccessfullyUpdatePatientPasswordById() {
    when(userRepository.findPatientByIdIfExists(any())).thenReturn(user);
    when(userConverter.convertToPatientDto(any())).thenReturn(patientDto);
    assertDoesNotThrow(
        () -> patientService.updatePasswordByPatientId(
            2L, passwordEncoder.encode("cat_cat_cat")));
  }

  @Test
  public void should_SuccessfullyDisablePatient() {
    patientDto.setDisabled(true);
    when(userRepository.findPatientByIdIfExists(any())).thenReturn(user);
    when(userConverter.convertToPatientDto(any())).thenReturn(patientDto);
    assertEquals(patientService.disablePatientById(2L), patientDto);
  }
}
