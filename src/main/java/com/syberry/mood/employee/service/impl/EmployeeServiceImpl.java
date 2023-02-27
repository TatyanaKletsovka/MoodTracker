package com.syberry.mood.employee.service.impl;

import static com.syberry.mood.authorization.util.SecurityUtils.getUserDetails;

import com.syberry.mood.authorization.security.UserDetailsImpl;
import com.syberry.mood.employee.converter.EmployeeConverter;
import com.syberry.mood.employee.dto.EmployeeCreatingDto;
import com.syberry.mood.employee.dto.EmployeeDto;
import com.syberry.mood.employee.dto.EmployeeUpdatingDto;
import com.syberry.mood.employee.dto.PasswordUpdatingDto;
import com.syberry.mood.employee.entity.Employee;
import com.syberry.mood.employee.repository.EmployeeRepository;
import com.syberry.mood.employee.service.EmployeeService;
import com.syberry.mood.employee.validation.EmployeeValidator;
import com.syberry.mood.user.converter.RoleConverter;
import com.syberry.mood.user.converter.UserConverter;
import com.syberry.mood.user.entity.User;
import com.syberry.mood.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is responsible for working with the table "employee".
 */
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final UserRepository userRepository;
  private final RoleConverter roleConverter;
  private final UserConverter userConverter;
  private final EmployeeConverter employeeConverter;
  private final EmployeeValidator employeeValidator;
  private final PasswordEncoder passwordEncoder;

  /**
   * Retrieves the list of employees.
   *
   * @return the list of employees
   */
  @Override
  public List<EmployeeDto> findAllEmployees() {
    return employeeRepository.findAll()
        .stream()
        .map(employeeConverter::convertToDto)
        .collect(Collectors.toList());
  }

  /**
   * Retrieves the employee by ID.
   *
   * @param id employee ID to search for data
   * @return employee
   */
  @Override
  public EmployeeDto findEmployeeById(Long id) {
    return employeeConverter.convertToDto(
        employeeRepository.findByIdIfExists(id));
  }

  /**
   * Retrieves information about the currently authenticated user
   * with role "admin" or "moderator".
   *
   * @return employee
   */
  @Override
  public EmployeeDto findEmployeeProfile() {
    return employeeConverter.convertToDto(
        employeeRepository.findByUserIdIfExist(getUserDetails().getId()));
  }

  /**
   * Adds a new employee to the table.
   *
   * @param dto an object containing information about the employee
   * @return employee
   */
  @Transactional
  @Override
  public EmployeeDto createEmployee(EmployeeCreatingDto dto) {
    employeeValidator.validateEmailUniqueness(dto.getEmail());
    employeeValidator.validateRoleForEmployee(dto.getRoleName());
    User user = userConverter.convertToEntity(dto);
    user.setPassword(passwordEncoder.encode(dto.getPassword()));
    userRepository.save(user);
    Employee employee = employeeConverter.convertToEntity(dto);
    employee.setUser(user);
    return employeeConverter.convertToDto(employeeRepository.save(employee));
  }

  /**
   * Updates the record about the user with the specified ID completely.
   *
   * @param dto an object containing information about the employee
   * @return dto
   */
  @Transactional
  @Override
  public EmployeeDto updateEmployeeById(EmployeeUpdatingDto dto) {
    Long id = dto.getId();
    Employee employee = employeeRepository.findByIdIfExists(id);
    employeeValidator.validateIsDisabled(employee);
    String email = dto.getEmail();
    String role = dto.getRoleName();
    User user = employee.getUser();
    if (!email.equals(user.getUsername())) {
      employeeValidator.validateEmailUniqueness(email);
    }
    employeeValidator.validateRoleForEmployee(role);
    if (roleConverter.convertToEntity(role) != user.getRole()) {
      employeeValidator.validateItIsNotSuperAdmin(employee);
    }
    return employeeConverter.convertToDto(employeeConverter.convertToEntity(dto));
  }

  /**
   * Changes the employee status (enabled/disabled) to the opposite.
   *
   * @param id employee id to change status
   * @return dto
   */
  @Transactional
  @Override
  public EmployeeDto toggleEmployeeDisabledStateById(Long id) {
    Employee employee = employeeRepository.findByIdIfExists(id);
    employeeValidator.validateItIsNotSuperAdmin(employee);
    User user = employee.getUser();
    user.setDisabled(!user.isDisabled());
    user.setUpdatedAt(LocalDateTime.now());
    return employeeConverter.convertToDto(employee);
  }

  /**
   * Updates the password for the currently authenticated user.
   *
   * @param dto an object containing the old and new password
   */
  @Override
  @Transactional
  public void updateEmployeePassword(PasswordUpdatingDto dto) {
    employeeValidator.validateCurrentPassword(dto.getOldPassword());
    UserDetailsImpl userDetails = getUserDetails();
    User user = userRepository.findUserByIdIfExists(userDetails.getId());
    user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    user.setUpdatedAt(LocalDateTime.now());
  }
}
