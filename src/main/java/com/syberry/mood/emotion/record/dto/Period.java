package com.syberry.mood.emotion.record.dto;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * An enumeration representing different periods of the day.
 */
@Getter
public enum Period {

  MORNING(LocalTime.of(0, 0), LocalTime.of(12, 0)),
  AFTERNOON(LocalTime.of(12, 0), LocalTime.of(17, 0)),
  EVENING(LocalTime.of(17, 0), LocalTime.of(0, 0));

  private final LocalTime periodStartTime;
  private final LocalTime periodEndTime;

  Period(LocalTime periodStartTime, LocalTime periodEndTime) {
    this.periodStartTime = periodStartTime;
    this.periodEndTime = periodEndTime;
  }

  /**
   * Determines the current period of the day based on the current time.
   *
   * @return the current period of the day (morning, afternoon, evening)
   */
  public static Period findOutCurrentPeriod() {
    if (isInPeriod(Period.MORNING)) {
      return Period.MORNING;
    } else if (isInPeriod(Period.AFTERNOON)) {
      return Period.AFTERNOON;
    } else {
      return Period.EVENING;
    }
  }

  /**
   * Determines whether the current time is within the specified period of the day.
   *
   * @param period the period of the day to check
   * @return true if the current time is within the specified period, false otherwise
   */
  private static boolean isInPeriod(Period period) {
    LocalTime currentTime = LocalTime.now();
    return currentTime.compareTo(period.periodStartTime) >= 0
        && currentTime.isBefore(period.periodEndTime);
  }

  /**
   * Returns a string containing the names of all periods.
   *
   * @return a string containing the names of all periods
   */
  public static String getNames() {
    return Arrays.stream(Period.class.getEnumConstants()).map(Enum::name)
        .collect(Collectors.joining(", "));
  }
}
