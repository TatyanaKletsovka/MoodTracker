package com.syberry.mood.emotion.record.dto;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * An enumeration representing different emotions.
 */
public enum Emotion {

  HAPPY,
  SAD,
  EXCITED,
  ANGRY,
  ANXIOUS,
  GROUCHY,
  RELAXED;

  /**
   * Returns a string containing the names of all emotions.
   *
   * @return a string containing the names of all emotions
   */
  public static String getNames() {
    return Arrays.stream(Emotion.class.getEnumConstants()).map(Enum::name)
        .collect(Collectors.joining(", "));
  }
}
