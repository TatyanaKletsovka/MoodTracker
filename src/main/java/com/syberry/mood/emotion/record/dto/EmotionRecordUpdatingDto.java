package com.syberry.mood.emotion.record.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A data transfer object that represents an emotion record for updating submitted by an admin.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmotionRecordUpdatingDto {

  private Long id;
  @NotEmpty
  private String emotion;
  @NotNull
  @Min(value = 1)
  @Max(value = 5)
  private int intensity;
  private String note;
}
