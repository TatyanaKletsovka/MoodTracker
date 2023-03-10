package com.syberry.mood.authorization.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.syberry.mood.authorization.dto.EmailDetailsDto;
import javax.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

  @InjectMocks
  private EmailServiceImpl emailService;
  @Mock
  private JavaMailSender mailSender;
  @Mock
  private SpringTemplateEngine templateEngine;
  @Mock
  private MimeMessage mimeMessage;

  private static final String EMAIL = "doc@gmail.com";
  private static final String TOKEN = "9b61291e-975e-41dd-992d-3dc0ab3a41b8";

  @Test
  void constructResetTokenEmailWhenCalledThenProcessTemplate() {
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    when(templateEngine.process(anyString(), any(Context.class))).thenReturn("test");
    doNothing().when(mailSender).send((MimeMessage) any());

    emailService.constructResetTokenEmail(TOKEN, EMAIL);

    verify(templateEngine).process(anyString(), any());
  }

  @Test
  void sendEmailWhenCalledThenSuccessfullySendEmail() {
    EmailDetailsDto dto = new EmailDetailsDto(EMAIL, "message", "Reset Password");
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

    emailService.sendEmail(dto);

    verify(mailSender, times(1)).send(any(MimeMessage.class));
  }
}
