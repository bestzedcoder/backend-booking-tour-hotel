package com.bestzedcoder.project3.booking_tour_hotel.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class MailService implements IEmailService {
  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  @Async
  public void sendVerificationEmail(MailDetails mailDetails) {
    Context context = new Context();
    context.setVariable("fullName", mailDetails.getUsername());
    context.setVariable("code", mailDetails.getToken());

    String htmlContent = templateEngine.process("verify_account", context);

    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setTo(mailDetails.getTo());
      helper.setSubject("Xác thực tài khoản của bạn");
      helper.setText(htmlContent, true);

      mailSender.send(message);
    } catch (MessagingException e) {
      throw new RuntimeException("Gửi email thất bại", e);
    }

  }

  @Override
  public void sendInfoUserDetails(MailDetails mailDetails) {
    Context context = new Context();
    context.setVariable("fullName", mailDetails.getFullName());
    context.setVariable("username", mailDetails.getUsername());
    context.setVariable("password", mailDetails.getRawPassword());

    String htmlContent = templateEngine.process("account_created", context);

    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setTo(mailDetails.getTo());
      helper.setSubject("Thông tin tài khoản của bạn");
      helper.setText(htmlContent, true);
      mailSender.send(message);
    } catch (MessagingException e) {
      throw new RuntimeException("Gửi email thất bại", e);
    }

  }
}
