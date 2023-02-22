package com.syberry.mood.user.validation;

import com.syberry.mood.exception.ValidationException;
import com.syberry.mood.user.entity.User;
import com.syberry.mood.user.repository.UserRepository;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * A service class for validating input related to patients.
 */
@Service
@RequiredArgsConstructor
public class PatientValidator {

  private final UserRepository userRepository;

  /**
   * Validates if the user is disabled and throws a ValidationException if it is.
   *
   * @param user the user to validate
   * @throws ValidationException if the user is disabled
   */
  public void validateUpdating(User user) {
    if (user.isDisabled()) {
      throw new ValidationException("Disabled user can't be updated");
    }
  }

  /**
   * Validates if the given superhero name is already taken
   * by another user and throws a ValidationException if it is.
   *
   * @param username the superhero name to validate
   * @param id       the ID of the user to update, null if creating a new user
   * @throws ValidationException if the superhero name is already taken by another user
   */
  public void validateSuperheroName(String username, Long id) {
    Optional<User> optionalByUsername = userRepository.findByUsername(username);
    if (optionalByUsername.isPresent() && !Objects.equals(optionalByUsername.get().getId(), id)) {
      throw new ValidationException(
          String.format("SuperheroName: %s is already taken. Try another one", username));
    }
  }
}
