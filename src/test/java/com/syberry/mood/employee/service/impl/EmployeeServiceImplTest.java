package com.syberry.mood.employee.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.syberry.mood.authorization.security.UserDetailsImpl;
import com.syberry.mood.employee.converter.EmployeeConverter;
import com.syberry.mood.employee.dto.EmployeeCreatingDto;
import com.syberry.mood.employee.dto.EmployeeDto;
import com.syberry.mood.employee.dto.EmployeeUpdatingDto;
import com.syberry.mood.employee.entity.Employee;
import com.syberry.mood.employee.repository.EmployeeRepository;
import com.syberry.mood.employee.validation.EmployeeValidator;
import com.syberry.mood.exception.EntityNotFoundException;
import com.syberry.mood.exception.ValidationException;
import com.syberry.mood.user.converter.RoleConverter;
import com.syberry.mood.user.converter.UserConverter;
import com.syberry.mood.user.dto.RoleName;
import com.syberry.mood.user.entity.Role;
import com.syberry.mood.user.entity.User;
import com.syberry.mood.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
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
class EmployeeServiceImplTest {

  @InjectMocks
  @MockitoSettings(strictness = Strictness.LENIENT)
  private EmployeeServiceImpl employeeService;
  @Mock
  private EmployeeRepository employeeRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private EmployeeConverter employeeConverter;
  @Mock
  private EmployeeValidator employeeValidator;
  @Mock
  private UserConverter userConverter;
  @Mock
  private RoleConverter roleConverter;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private SecurityContext securityContext;
  @Mock
  private Authentication authentication;

  private User user = new User();
  private Employee employee = new Employee();
  private EmployeeDto employeeDto = new EmployeeDto();
  private EmployeeCreatingDto employeeCreatingDto = new EmployeeCreatingDto();
  private EmployeeUpdatingDto employeeUpdatingDto = new EmployeeUpdatingDto();

  private Role roleAdmin = new Role(1L, RoleName.ADMIN);

  private static final String USER = "USER";
  private static final String EMAIL_FIRST = "test1@test.com";
  private static final String EMAIL_SECOND = "test2@test.com";
  private static final String PASSWORD = "test1Test";
  private static final Long ID_FIRST = 1L;
  private static final String FIRST_NAME = "Firstname";
  private static final String LAST_NAME = "Lastname";

  @BeforeEach
  public void init() {

    user.setId(ID_FIRST);
    user.setUsername(EMAIL_FIRST);
    user.setPassword(PASSWORD);
    user.setRole(roleAdmin);
    user.setCreatedAt(LocalDateTime.now());
    user.setDisabled(false);
    employee.setId(ID_FIRST);
    employee.setFirstName(FIRST_NAME);
    employee.setLastName(LAST_NAME);
    employee.setUser(user);

    employeeDto.setId(ID_FIRST);
    employeeDto.setEmail(EMAIL_FIRST);
    employeeDto.setFirstName(FIRST_NAME);
    employeeDto.setLastName(LAST_NAME);
    employeeDto.setRoleName(RoleName.ADMIN);
    employeeDto.setDisabled(false);

    employeeUpdatingDto.setId(1L);
    employeeUpdatingDto.setRoleName(USER);
    employeeUpdatingDto.setEmail(EMAIL_SECOND);
  }

  @Test
  void findAllEmployeesWhenCalledThenReturnsListOfEmployeeDto() {
    when(employeeRepository.findAll()).thenReturn(List.of(employee));
    when(employeeConverter.convertToDto(any())).thenReturn(employeeDto);

    List<EmployeeDto> actualResult = employeeService.findAllEmployees();

    assertEquals(List.of(employeeDto), actualResult);
  }

  @Test
  void findEmployeeByIdWhenEmployeeFoundedThenReturnEmployeeDto() {
    when(employeeRepository.findByIdIfExists(any())).thenReturn(employee);
    when(employeeConverter.convertToDto(any())).thenReturn(employeeDto);

    EmployeeDto actualResult = employeeService.findEmployeeById(ID_FIRST);

    assertEquals(employeeDto, actualResult);
  }

  @Test
  void findEmployeeByIdWhenEmployeeNotFoundedThenThrowException() {
    when(employeeRepository.findByIdIfExists(any())).thenThrow(EntityNotFoundException.class);

    assertThrows(EntityNotFoundException.class, () -> employeeService.findEmployeeById(ID_FIRST));
  }

  @Test
  void createEmployeeWhenRoleAndEmailCorrectThenReturnEmployeeDto() {
    when(employeeConverter.convertToEntity((EmployeeCreatingDto) any())).thenReturn(employee);
    when(userConverter.convertToEntity(any(EmployeeCreatingDto.class))).thenReturn(user);
    when(userRepository.save(any())).thenReturn(user);
    when(employeeConverter.convertToDto(any())).thenReturn(employeeDto);
    doNothing().when(employeeValidator).validateRoleForEmployee(any());
    doNothing().when(employeeValidator).validateEmailUniqueness(any());

    assertEquals(employeeDto, employeeService.createEmployee(employeeCreatingDto));
  }

  @Test
  void createEmployeeWhenRoleIncorrectThenThrowException() {
    doThrow(ValidationException.class).when(employeeValidator)
        .validateRoleForEmployee(any());

    assertThrows(ValidationException.class,
        () -> employeeService.createEmployee(employeeCreatingDto));
  }

  @Test
  void createEmployeeWhenEmailInUseThenThrowException() {
    doThrow(ValidationException.class).when(employeeValidator).validateEmailUniqueness(any());

    assertThrows(ValidationException.class,
        () -> employeeService.createEmployee(employeeCreatingDto));
  }

  @Test
  void updateEmployeeByIdWhenRoleAndEmailCorrectThenReturnEmployeeDto() {
    doNothing().when(employeeValidator).validateEmailUniqueness(any());
    doNothing().when(employeeValidator).validateRoleForEmployee(any());
    when(employeeRepository.findByIdIfExists(any())).thenReturn(employee);
    when(employeeConverter.convertToDto(any())).thenReturn(employeeDto);

    assertEquals(employeeDto, employeeService.updateEmployeeById(employeeUpdatingDto));
  }

  @Test
  void updateEmployeeByIdWhenRoleIsUserThenThrowException() {
    when(employeeRepository.findByIdIfExists(any())).thenReturn(employee);
    doThrow(ValidationException.class).when(employeeValidator)
        .validateRoleForEmployee(any());

    assertThrows(ValidationException.class,
        () -> employeeService.updateEmployeeById(employeeUpdatingDto));
  }

  @Test
  void updateEmployeeByIdWhenRoleIsSuperAdminAndChangeRoleThenThrowException() {
    when(employeeRepository.findByIdIfExists(any())).thenReturn(employee);
    doThrow(ValidationException.class).when(employeeValidator).validateItIsNotSuperAdmin(any());

    assertThrows(ValidationException.class,
        () -> employeeService.updateEmployeeById(employeeUpdatingDto));
  }

  @Test
  void updateEmployeeByIdWhenEmployeeIsDisabledThenThrowException() {
    doThrow(ValidationException.class).when(employeeValidator).validateIsDisabled(any());

    assertThrows(ValidationException.class,
        () -> employeeService.updateEmployeeById(employeeUpdatingDto));
  }

  @Test
  void disableEmployeeByIdWhenValidationIdOkThenReturnEmployeeDto() {
    doNothing().when(employeeValidator).validateItIsNotSuperAdmin(any());
    when(employeeConverter.convertToDto(any())).thenReturn(employeeDto);
    when(employeeRepository.findByIdIfExists(any())).thenReturn(employee);

    assertEquals(employeeDto, employeeService.toggleEmployeeDisabledStateById(any()));
  }

  @Test
  void disableEmployeeByIdWhenRoleIsSuperAdminThenThrowException() {
    doThrow(ValidationException.class).when(employeeValidator).validateItIsNotSuperAdmin(any());

    assertThrows(ValidationException.class, () -> employeeService.toggleEmployeeDisabledStateById(ID_FIRST));
  }

  @Test
  public void should_SuccessfullyFindEmployeeProfile() {
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(authentication.getPrincipal())
        .thenReturn(new UserDetailsImpl(1L, "doc@gmail.com", "AbSdA_21sSA",
            new SimpleGrantedAuthority(RoleName.ADMIN.name())));
    when(employeeRepository.findByUserIdIfExist(any())).thenReturn(employee);
    when(employeeConverter.convertToDto(any())).thenReturn(employeeDto);
    assertEquals(employeeService.findEmployeeProfile(), employeeDto);
  }
}
