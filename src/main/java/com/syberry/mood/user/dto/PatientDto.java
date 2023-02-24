package com.syberry.mood.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A data transfer object that represents a Patient entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDto {

  private Long id;
  private String superheroName;
  private boolean disabled;
}
