package com.syberry.mood.authorization.service.impl;

import com.syberry.mood.authorization.dto.EmailDetailsDto;
import com.syberry.mood.authorization.service.EmailService;
import com.syberry.mood.exception.EmailException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

/**
 * Implementation of the EmailService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
  private final JavaMailSender mailSender;
  private final SpringTemplateEngine templateEngine;

  @Value("${app.name}")
  private String appName;
  @Value("${app.reset-password-url}")
  private String resetUrl;
  @Value("${app.cache-expiration}")
  private String expiration;

  @Override
  public void sendEmail(EmailDetailsDto dto) {
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
    try {
      helper.setTo(dto.getRecipient());
      helper.setSubject(dto.getSubject());
      helper.setText(dto.getMsgBody(), true);
      mailSender.send(mimeMessage);
    } catch (MessagingException e) {
      throw new EmailException(e, "Failed to send an email");
    }
  }

  @Override
  public void constructResetTokenEmail(String token, String userEmail) {
    String link = resetUrl + "?token=" + token + "&email=" + userEmail;
    Context context = new Context();
    context.setVariable("link", link);
    context.setVariable("application", appName);
    context.setVariable("expiration", expiration);
    String emailContent = templateEngine.process("reset-password", context);
    sendEmail(new EmailDetailsDto(userEmail, emailContent, "Reset Password"));
  }
}
