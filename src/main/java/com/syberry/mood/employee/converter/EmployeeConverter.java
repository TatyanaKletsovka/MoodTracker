package com.syberry.mood.employee.converter;

import static org.springframework.util.StringUtils.capitalize;

import com.syberry.mood.employee.dto.EmployeeCreatingDto;
import com.syberry.mood.employee.dto.EmployeeDto;
import com.syberry.mood.employee.dto.EmployeeUpdatingDto;
import com.syberry.mood.employee.entity.Employee;
import com.syberry.mood.employee.repository.EmployeeRepository;
import com.syberry.mood.user.converter.RoleConverter;
import com.syberry.mood.user.entity.User;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for converting DTO objects
 * containing information about employees into their entity
 * counterparts and from DTOs into entities.
 */
@Component
@RequiredArgsConstructor
public class EmployeeConverter {

  private final RoleConverter converter;
  private final EmployeeRepository employeeRepository;

  /**
   * Converts Employee JPA Entity into EmployeeDto.
   *
   * @param employee Employee JPA Entity
   * @return EmployeeDto
   */
  public EmployeeDto convertToDto(Employee employee) {
    return EmployeeDto.builder()
        .id(employee.getId())
        .email(employee.getUser().getUsername())
        .firstName(employee.getFirstName())
        .lastName(employee.getLastName())
        .roleName(employee.getUser().getRole().getRoleName())
        .disabled(employee.getUser().isDisabled())
        .build();
  }

  /**
   * Converts EmployeeCreationDto into Employee JPA Entity.
   *
   * @param dto EmployeeCreationDto
   * @return Employee JPA Entity
   */
  public Employee convertToEntity(EmployeeCreatingDto dto) {
    return Employee.builder()
        .firstName(capitalize(dto.getFirstName()))
        .lastName(capitalize(dto.getLastName()))
        .build();
  }

  /**
   * Converts EmployeeUpdatingDto into Employee JPA Entity.
   *
   * @param dto EmployeeUpdatingCreationDto
   * @return Employee JPA Entity
   */
  public Employee convertToEntity(EmployeeUpdatingDto dto) {
    Employee employee = employeeRepository.findByIdIfExists(dto.getId());
    User user = employee.getUser();
    user.setUsername(dto.getEmail());
    user.setRole(converter.convertToEntity(dto.getRoleName()));
    user.setUpdatedAt(LocalDateTime.now());
    employee.setFirstName(capitalize(dto.getFirstName()));
    employee.setLastName(capitalize(dto.getLastName()));
    return employee;
  }
}
