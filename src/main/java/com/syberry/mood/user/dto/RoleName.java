package com.syberry.mood.user.dto;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * An enumeration of available roles in the application.
 */
public enum RoleName {

  ADMIN,
  MODERATOR,
  USER;

  public static String getNames() {
    return Arrays.stream(RoleName.class.getEnumConstants()).map(Enum::name)
        .collect(Collectors.joining(", "));
  }
}
