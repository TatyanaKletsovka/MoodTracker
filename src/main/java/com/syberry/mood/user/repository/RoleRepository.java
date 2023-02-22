package com.syberry.mood.user.repository;

import com.syberry.mood.exception.EntityNotFoundException;
import com.syberry.mood.user.dto.RoleName;
import com.syberry.mood.user.entity.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing roles in the database.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

  /**
   * Finds a role by its name.
   *
   * @param roleName the name of the role to find
   * @return an Optional containing the role if it exists, or an empty Optional if it does not
   */
  Optional<Role> findByRoleName(RoleName roleName);

  /**
   * Finds a role by its name and throws an EntityNotFoundException if it does not exist.
   *
   * @param roleName the name of the role to find
   * @return the role if it exists
   * @throws EntityNotFoundException if the role does not exist
   */
  default Role findByRoleNameIfExists(RoleName roleName) {
    return findByRoleName(roleName).orElseThrow(()
        -> new EntityNotFoundException(String.format("Role with name: %s is not found", roleName)));
  }
}
