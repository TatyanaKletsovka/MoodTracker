package com.syberry.mood.emotion.record.converter;

import com.syberry.mood.emotion.record.dto.Emotion;
import com.syberry.mood.exception.InvalidArgumentTypeException;
import org.springframework.stereotype.Component;

/**
 * A component that provides converting method for the Emotion enum.
 */
@Component
public class EmotionConverter {

  /**
   * Converts a string representation of an emotion into an Emotion enum value.
   *
   * @param emotion a string representation of an emotion
   * @return an Emotion enum value
   * @throws InvalidArgumentTypeException if the input string is not a valid emotion
   */
  public Emotion convertToEnum(String emotion) {
    try {
      return Emotion.valueOf(emotion.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new InvalidArgumentTypeException(
          String.format("Error while converting invalid emotion: %s. Valid emotions: %s",
              emotion, Emotion.getNames()));
    }
  }
}
