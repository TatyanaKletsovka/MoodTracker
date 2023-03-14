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

  MORNING(LocalTime.of(0, 0), LocalTime.of(12, 0), 3),
  AFTERNOON(LocalTime.of(12, 0), LocalTime.of(17, 0), 2),
  EVENING(LocalTime.of(17, 0), LocalTime.of(0, 0), 1);

  private final LocalTime periodStartTime;
  private final LocalTime periodEndTime;
  private final int periodsAfter;

  Period(LocalTime periodStartTime, LocalTime periodEndTime, int periodsAfter) {
    this.periodStartTime = periodStartTime;
    this.periodEndTime = periodEndTime;
    this.periodsAfter = periodsAfter;
  }

  /**
   * Determines the period of the day based on a given time.
   *
   * @param localTime the time to use for determining the period
   * @return the current period of the day (morning, afternoon, evening)
   */
  public static Period findOutPeriodByTime(LocalTime localTime) {
    if (isInPeriod(Period.MORNING, localTime)) {
      return Period.MORNING;
    } else if (isInPeriod(Period.AFTERNOON, localTime)) {
      return Period.AFTERNOON;
    } else {
      return Period.EVENING;
    }
  }

  /**
   * Determines whether the time is within the specified period of the day.
   *
   * @param period the period of the day to check
   * @param time the time to check
   * @return true if the time is within the specified period, false otherwise
   */
  private static boolean isInPeriod(Period period, LocalTime time) {
    return time.compareTo(period.periodStartTime) >= 0
        && time.isBefore(period.periodEndTime);
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

  /**
   * Returns the number of periods after the current period inclusive.
   *
   * @return the number of periods after the current period inclusive
   */
  public int getPeriodsAfter() {
    return periodsAfter;
  }

  /**
   * Counts the number of periods before the current period inclusive.
   *
   * @return the number of periods before the current period inclusive
   */
  public int countPeriodsBefore() {
    return Arrays.stream(Period.values())
        .filter(p -> p != this)
        .mapToInt(Period::getPeriodsAfter)
        .max()
        .orElse(0);
  }

  /**
   * Counts the number of periods between the current period and the specified period inclusive.
   *
   * @param period the specified period
   * @return the number of periods between the current period and the specified period inclusive
   */
  public int countDayPeriodsBetween(Period period) {
    if (this == period) {
      return 1;
    } else {
      return Math.abs(this.getPeriodsAfter() - period.getPeriodsAfter()) + 1;
    }
  }
}
