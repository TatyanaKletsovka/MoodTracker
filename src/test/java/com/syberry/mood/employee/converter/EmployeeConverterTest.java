package com.syberry.mood.employee.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.syberry.mood.employee.dto.EmployeeCreatingDto;
import com.syberry.mood.employee.dto.EmployeeDto;
import com.syberry.mood.employee.dto.EmployeeUpdatingDto;
import com.syberry.mood.employee.entity.Employee;
import com.syberry.mood.employee.repository.EmployeeRepository;
import com.syberry.mood.user.converter.RoleConverter;
import com.syberry.mood.user.dto.RoleName;
import com.syberry.mood.user.entity.Role;
import com.syberry.mood.user.entity.User;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeConverterTest {

  @InjectMocks
  private EmployeeConverter employeeConverter;

  @Mock
  private RoleConverter roleConverter;

  @Mock
  private EmployeeRepository employeeRepository;
  private Employee employee;
  private static final String LASTNAME = "Lastname";
  private static final String NEW_LASTNAME = "NewLastname";
  private static final String FIRSTNAME = "Firstname";
  private static final String NEW_FIRSTNAME = "NewFirstname";
  private static final Long ID = 2L;
  private static final String USERNAME = "test@gmail.com";
  private static final String NEW_USERNAME = "newtest@gmail.com";
  private static final String PASSWORD = "test1Password";
  private static final String ROLE_NAME = "MODERATOR";

  @BeforeEach
  public void init() {
    employee = createEmployee(FIRSTNAME, LASTNAME,
        createUser(USERNAME, RoleName.ADMIN));
  }

  @Test
  void convertToDtoWhenConvertFromEmployeeThenReturnEmployeeDto() {
    EmployeeDto employeeDto = new EmployeeDto();
    employeeDto.setId(ID);
    employeeDto.setEmail(USERNAME);
    employeeDto.setFirstName(FIRSTNAME);
    employeeDto.setLastName(LASTNAME);
    employeeDto.setRoleName(RoleName.ADMIN);
    employeeDto.setDisabled(false);

    assertEquals(employeeDto, employeeConverter.convertToDto(employee));
  }

  @Test
  void convertToEntityWhenConvertFromEmployeeCreationDtoThenReturnEmployee() {
    EmployeeCreatingDto employeeCreatingDto = new EmployeeCreatingDto();
    employeeCreatingDto.setFirstName(FIRSTNAME);
    employeeCreatingDto.setLastName(LASTNAME);

    assertThat(employeeConverter.convertToEntity(employeeCreatingDto))
        .usingRecursiveComparison()
        .ignoringFields("id", "user")
        .isEqualTo(employee);
  }

  @Test
  void convertToEntityWhenConvertFromEmployeeUpdatingDtoThenReturnEmployee() {
    EmployeeUpdatingDto employeeUpdatingDto = new EmployeeUpdatingDto();
    employeeUpdatingDto.setFirstName(NEW_FIRSTNAME);
    employeeUpdatingDto.setLastName(NEW_LASTNAME);
    employeeUpdatingDto.setEmail(NEW_USERNAME);
    employeeUpdatingDto.setRoleName(ROLE_NAME);
    Employee updatedEmployee = createEmployee(NEW_FIRSTNAME, NEW_LASTNAME,
        createUser(NEW_USERNAME, RoleName.MODERATOR));
    when(employeeRepository.findByIdIfExists(any())).thenReturn(employee);
    when(roleConverter.convertToEntity((String) any()))
        .thenReturn(new Role(ID, RoleName.MODERATOR));

    assertThat(employeeConverter.convertToEntity(employeeUpdatingDto))
        .usingRecursiveComparison()
        .ignoringFields("id", "user.createdAt", "user.updatedAt")
        .isEqualTo(updatedEmployee);
  }

  private Employee createEmployee(String firstname, String lastname, User user) {
    return Employee.builder()
        .id(ID)
        .firstName(firstname)
        .lastName(lastname)
        .user(user)
        .build();
  }

  private User createUser(String username, RoleName roleName) {
    return User.builder()
        .id(ID)
        .username(username)
        .password(PASSWORD)
        .role(new Role(ID, roleName))
        .createdAt(LocalDateTime.now())
        .disabled(false)
        .build();
  }
}
