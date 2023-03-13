package com.syberry.mood.emotion.record.converter;

import com.syberry.mood.emotion.record.dto.Period;
import com.syberry.mood.exception.InvalidArgumentTypeException;
import org.springframework.stereotype.Component;

/**
 * A component that provides converting method for the Period enum.
 */
@Component
public class PeriodConverter {

  /**
   * Converts a string representation of a period into a Period enum value.
   *
   * @param period a string representation of a period
   * @return a Period enum value
   * @throws InvalidArgumentTypeException if the input string is not a valid period
   */
  public Period convertToEnum(String period) {
    try {
      return Period.valueOf(period.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new InvalidArgumentTypeException(
          String.format("Error while converting invalid period: %s. Valid periods: %s",
              period, Period.getNames()));
    }
  }
}
