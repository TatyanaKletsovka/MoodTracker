package com.syberry.mood.emotion.record.dto;

import java.time.LocalDate;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * A data transfer object that represents an emotion record for creation submitted by an admin.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EmotionRecordCreationDto extends EmotionRecordByPatientDto {

  private String note;
  private Long patientId;
  @NotNull
  @Past(message = "Date of emotion record can't be in future")
  private LocalDate date;
  @NotEmpty
  private String period;
}
