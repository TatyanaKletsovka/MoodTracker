package com.syberry.mood.emotion.record.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;

/**
 * A utility class for working with dates and times.
 */
@UtilityClass
public class DateUtil {

  /**
   * Converts a LocalDate to LocalDateTime representing the end of the day.
   *
   * @param date the LocalDate to be converted.
   * @return the LocalDateTime representing the end of the day.
   */
  public static LocalDateTime convertToDateTimeEndDay(LocalDate date) {
    return date.plusDays(1).atStartOfDay().minusNanos(1);
  }
}
