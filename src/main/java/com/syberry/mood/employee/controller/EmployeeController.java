package com.syberry.mood.employee.controller;

import com.syberry.mood.employee.dto.EmployeeCreatingDto;
import com.syberry.mood.employee.dto.EmployeeDto;
import com.syberry.mood.employee.dto.EmployeeUpdatingDto;
import com.syberry.mood.employee.dto.PasswordUpdatingDto;
import com.syberry.mood.employee.service.EmployeeService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller handles queries against the "employee" table.
 */
@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EmployeeController {

  private final EmployeeService employeeService;

  /**
   * Retrieves the list of employees.
   *
   * @return the list of EmployeeDto
   */
  @GetMapping
  public List<EmployeeDto> findAllEmployees() {
    log.info("GET-request: getting all employees");
    return employeeService.findAllEmployees();
  }

  /**
   * Retrieves the employee by ID.
   *
   * @param id employee ID to search for data
   * @return EmployeeDto
   */
  @GetMapping("/{id}")
  public EmployeeDto findEmployeeById(@PathVariable("id") Long id) {
    log.info("GET-request: getting employee with id: {}", id);
    return employeeService.findEmployeeById(id);
  }

  /**
   * Retrieves information about the currently authenticated user
   * with role "admin" or "moderator".
   *
   * @return EmployeeDto
   */
  @GetMapping("/profile")
  public EmployeeDto findEmployeeProfile() {
    log.info("GET-request: getting current employee profile");
    return employeeService.findEmployeeProfile();
  }

  /**
   * Adds a new employee to the table.
   *
   * @param dto an object containing information about the employee
   * @return EmployeeDto
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public EmployeeDto createEmployee(@Valid @RequestBody EmployeeCreatingDto dto) {
    log.info("POST-request: creating new employee");
    return employeeService.createEmployee(dto);
  }

  /**
   * Updates the record about the employee with the specified ID completely.
   *
   * @param id  employee ID to update data
   * @param dto an object containing information about the employee
   * @return EmployeeDto
   */
  @PutMapping("/{id}")
  public EmployeeDto updateEmployee(@PathVariable("id") Long id,
                                    @Valid @RequestBody EmployeeUpdatingDto dto) {
    log.info("PUT-request: updating employee with id: {}", id);
    dto.setId(id);
    return employeeService.updateEmployeeById(dto);
  }

  /**
   * Changes the employee status (enabled/disabled) to the opposite.
   *
   * @param id employee id to change status
   * @return EmployeeDto
   */
  @PutMapping("/{id}/disabled")
  public EmployeeDto toggleEmployeeDisabledStateById(@PathVariable("id") Long id) {
    log.info("PUT-request: reverse is employee disabled for employee with id: {}", id);
    return employeeService.toggleEmployeeDisabledStateById(id);
  }

  /**
   * Updates the password for the currently authenticated user.
   *
   * @param dto an object containing the old and new password
   */
  @PutMapping("/new-password")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateEmployeePassword(@Valid @RequestBody PasswordUpdatingDto dto) {
    log.info("PUT-request: updating password for the currently authenticated employee");
    employeeService.updateEmployeePassword(dto);
  }
}
