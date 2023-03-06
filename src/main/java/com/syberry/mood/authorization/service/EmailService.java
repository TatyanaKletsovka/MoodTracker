package com.syberry.mood.authorization.service;

import com.syberry.mood.authorization.dto.EmailDetailsDto;

/**
 * This interface is responsible for constructing messages and sending emails.
 */
public interface EmailService {

  /**
   * Sends html email.
   *
   * @param dto object containing the message and information about the recipient and the subject
   */
  void sendEmail(EmailDetailsDto dto);

  /**
   * Sends an email with a password reset link.
   *
   * @param token password reset token
   * @param userEmail email of the recipient
   */
  void constructResetTokenEmail(String token, String userEmail);
}
