package com.syberry.mood.employee.repository;

import com.syberry.mood.employee.entity.Employee;
import com.syberry.mood.exception.EntityNotFoundException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The repository for working with the 'employee' table.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

  /**
   * Finds employee by its user id.
   *
   * @param userId user id
   * @return an Optional containing employee if it exists or an empty Optional if it does not
   */
  Optional<Employee> findByUserId(Long userId);

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

  /**
   * Returns the employee by its user id or throws an exception if no employee is found.
   *
   * @param userId user id
   * @return Employee
   */
  default Employee findByUserIdIfExist(Long userId) {
    return findByUserId(userId).orElseThrow(
        () -> new EntityNotFoundException(
            String.format("Employee with userId %s is not found", userId)));
  }
}
