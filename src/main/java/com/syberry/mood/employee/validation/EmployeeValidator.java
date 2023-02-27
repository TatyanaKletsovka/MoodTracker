package com.syberry.mood.employee.validation;

import com.syberry.mood.authorization.security.UserDetailsImpl;
import com.syberry.mood.authorization.util.SecurityUtils;
import com.syberry.mood.employee.entity.Employee;
import com.syberry.mood.exception.ValidationException;
import com.syberry.mood.user.converter.RoleConverter;
import com.syberry.mood.user.dto.RoleName;
import com.syberry.mood.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * This class is responsible to validate objects.
 */
@Component
@RequiredArgsConstructor
public class EmployeeValidator {

  private final UserRepository userRepository;
  private final RoleConverter roleConverter;
  private final PasswordEncoder passwordEncoder;

  /**
   * Validates if email already in use and throws a ValidationException if it is.
   *
   * @param email email to validate
   * @throws ValidationException if email already in use
   */
  public void validateEmailUniqueness(String email) {
    if (userRepository.findByUsername(email).isPresent()) {
      throw new ValidationException(String.format(
          "The employee with the specified email address %s"
              + " already exists.", email));
    }
  }

  /**
   * Validates if the employee role is "SUPER_ADMIN" and the employee with
   * this role already exists, or if the employee role is "USER"
   * and throws a ValidationException if it is.
   *
   * @param role the employee id to validate
   * @throws ValidationException if the employee role is "SUPER_ADMIN" and the employee
   with this role already exists, or if the employee role is "USER"
   */
  public void validateRoleForEmployee(String role) {
    RoleName roleName = roleConverter.convertToEntity(role).getRoleName();
    if (roleName == RoleName.USER) {
      throw new ValidationException(String.format(
          "The employee cannot be assigned this role: %s. Valid roles: %s, %s.",
          RoleName.USER, RoleName.ADMIN, RoleName.MODERATOR));
    }
    if (roleName == RoleName.SUPER_ADMIN) {
      validateNoEmployeeWithTheSuperAdminRole();
    }
  }

  /**
   * Validates if the employee role is "SUPER_ADMIN" and throws a ValidationException if it is.
   *
   * @param employee the employee to validate
   * @throws ValidationException if the employee role is "SUPER_ADMIN"
   */
  public void validateItIsNotSuperAdmin(Employee employee) {
    if (employee.getUser().getRole().getRoleName() == RoleName.SUPER_ADMIN) {
      throw new ValidationException("Updating is not available"
          + " for the employee with the role 'SUPER_ADMIN'");
    }
  }

  /**
   * Validates if the employee is disabled and throws a ValidationException if it is.
   *
   * @param employee the employee to validate
   * @throws ValidationException if the employee is disabled
   */
  public void validateIsDisabled(Employee employee) {
    if (employee.getUser().isDisabled()) {
      throw new ValidationException("Disabled employee can't be updated");
    }
  }

  /**
   * Validates that stored in DB password equals to received one and
   * throws ValidationException if it doesn't.
   *
   * @param password received password
   * @throws ValidationException if passwords are not the same
   */
  public void validateCurrentPassword(String password) {
    UserDetailsImpl userDetails = SecurityUtils.getUserDetails();
    if (!passwordEncoder.matches(password, userDetails.getPassword())) {
      throw new ValidationException("Wrong password");
    }
  }

  /**
   * Validates that no employee has the role "SUPER_ADMIN"
   * and throws a ValidationException if it is.
   *
   * @throws ValidationException if there is an employee with the role "SUPER_ADMIN"
   */
  private void validateNoEmployeeWithTheSuperAdminRole() {
    if (userRepository.existsUserByRoleRoleNameIs(RoleName.SUPER_ADMIN)) {
      throw new ValidationException("The employee with the role 'SUPER_ADMIN' already exists:"
          + " can't create more than one employee with the 'SUPER_ADMIN' role");
    }
  }
}
