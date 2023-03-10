package com.syberry.mood.authorization.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The object containing the message and information about the recipient's email and email subject.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailDetailsDto {
  private String recipient;
  private String msgBody;
  private String subject;
}
