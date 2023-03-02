package com.syberry.mood.employee.repository;

import com.syberry.mood.employee.entity.Employee;
import com.syberry.mood.exception.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The repository for working with the 'employee' table.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

  /**
   * Returns the employee by id or throws an exception if no employee is found.
   *
   * @param id employee id
   * @return Employee
   */
  default Employee findByIdIfExists(Long id) {
    return findById(id).orElseThrow(
        () -> new EntityNotFoundException(
            String.format("Employee with id %s is not found", id)));
  }
}
