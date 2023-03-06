package com.syberry.mood.emotion.record.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * A data transfer object that represents an emotion record submitted by a patient.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EmotionRecordByPatientDto {

  @NotEmpty
  private String emotion;
  @NotNull
  @Min(value = 1)
  @Max(value = 5)
  private int intensity;
}
