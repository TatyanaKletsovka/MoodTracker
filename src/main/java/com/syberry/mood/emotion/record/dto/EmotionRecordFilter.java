package com.syberry.mood.emotion.record.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A filter for filtering emotion records by date range.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmotionRecordFilter {

  private LocalDate startDate = LocalDate.now();
  private LocalDate endDate = LocalDate.now();
}
