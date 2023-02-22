package com.syberry.mood.user.repository;

import com.syberry.mood.exception.EntityNotFoundException;
import com.syberry.mood.user.dto.RoleName;
import com.syberry.mood.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing users in the database.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * Finds all users with a given role name and sorts them.
   *
   * @param roleName the name of the role to filter users by
   * @param sort     the sorting criteria to apply
   * @return a list of users with the given role name, sorted as specified
   */
  List<User> findByRoleRoleName(RoleName roleName, Sort sort);

  /**
   * Finds a user by their username.
   *
   * @param username the username of the user to find
   * @return an Optional containing the user if it exists, or an empty Optional if it does not
   */
  Optional<User> findByUsername(String username);

  /**
   * Finds a user by their ID and the name of their role.
   *
   * @param id       the ID of the user to find
   * @param roleName the name of the role of the user to find
   * @return an Optional containing the user if it exists, or an empty Optional if it does not
   */
  Optional<User> findByIdAndRoleRoleName(Long id, RoleName roleName);

  /**
   * Checks if the user with a given role name exists.
   *
   * @param roleName the name of the role of the user to find
   * @return true if the user with a given role name exists, or false if it does not exist
   */
  boolean existsUserByRoleRoleNameIs(RoleName roleName);

  /**
   * Finds all patients in descending order by ID.
   *
   * @return a list of patients sorted in descending order by ID
   */
  default List<User> findAllPatientsSortIdDesc() {
    return findByRoleRoleName(RoleName.USER, Sort.by(Sort.Direction.DESC, "id"));
  }

  /**
   * Finds a patient by their ID and throws an EntityNotFoundException if they do not exist.
   *
   * @param id the ID of the patient to find
   * @return the patient with the given ID
   * @throws EntityNotFoundException if the patient does not exist
   */
  default User findPatientByIdIfExists(Long id) {
    return findByIdAndRoleRoleName(id, RoleName.USER).orElseThrow(()
        -> new EntityNotFoundException(String.format("Patient with id: %s is not found", id)));
  }
}
