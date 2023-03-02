package com.syberry.mood.employee.service;

import com.syberry.mood.employee.dto.EmployeeCreatingDto;
import com.syberry.mood.employee.dto.EmployeeDto;
import com.syberry.mood.employee.dto.EmployeeUpdatingDto;
import com.syberry.mood.employee.dto.PasswordUpdatingDto;
import java.util.List;

/**
 * This class is responsible for working with the table "employee".
 */
public interface EmployeeService {

  /**
   * Retrieves the list of employees.
   *
   * @return the list of employees
   */
  List<EmployeeDto> findAllEmployees();

  /**
   * Retrieves the employee by ID.
   *
   * @param id employee ID to search for data
   * @return employee
   */
  EmployeeDto findEmployeeById(Long id);

  /**
   * Retrieves information about the currently authenticated user
   * with role "admin" or "moderator".
   *
   * @return employee
   */
  EmployeeDto findEmployeeProfile();


  /**
   * Adds a new employee to the table.
   *
   * @param dto an object containing information about the employee
   * @return employee
   */
  EmployeeDto createEmployee(EmployeeCreatingDto dto);


  /**
   * Updates the record about the user with the specified ID completely.
   *
   * @param dto an object containing information about the employee
   * @return dto
   */
  EmployeeDto updateEmployeeById(EmployeeUpdatingDto dto);

  /**
   * Changes the employee status (enabled/disabled) to the opposite.
   *
   * @param id employee id to change status
   * @return dto
   */
  EmployeeDto toggleEmployeeDisabledStateById(Long id);

  /**
   * Updates the record about the employee with the specified ID completely.
   *
   * @param dto an object containing the old and new password
   */
  void updateEmployeePassword(PasswordUpdatingDto dto);
}
