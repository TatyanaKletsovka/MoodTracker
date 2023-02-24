package com.syberry.mood.user.dto;

import com.syberry.mood.user.util.Constants;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A data transfer object that represents a request to create a new Patient entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientCreationDto {

  @NotEmpty
  @Size(max = 50)
  @Pattern(regexp = Constants.SUPERHERO_NAME_REGEX,
      message = Constants.SUPERHERO_NAME_MESSAGE)
  private String superheroName;
  @NotEmpty
  @Pattern(regexp = Constants.PATIENT_PASSWORD_REGEX,
      message = Constants.PATIENT_PASSWORD_MESSAGE)
  private String password;
}
