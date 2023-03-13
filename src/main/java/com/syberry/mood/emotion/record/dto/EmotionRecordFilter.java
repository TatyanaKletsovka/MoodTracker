package com.syberry.mood.emotion.record.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A data transfer object that represents filter parameters.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmotionRecordFilter {

  private LocalDate startDate = LocalDate.now();
  private LocalDate endDate = LocalDate.now();
}
