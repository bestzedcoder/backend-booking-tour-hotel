package com.bestzedcoder.project3.booking_tour_hotel.rabbit;

import com.bestzedcoder.project3.booking_tour_hotel.config.RabbitConfig;
import com.bestzedcoder.project3.booking_tour_hotel.enums.EmailType;
import com.bestzedcoder.project3.booking_tour_hotel.mail.IEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailConsumer {
  private final IEmailService emailService;

  @RabbitListener(
      queues = RabbitConfig.EMAIL_QUEUE,
      concurrency = "3"
  )
  public void consume(EmailMessage emailMessage) {
    if (emailMessage.getMessageType().equals(EmailType.CREATE_USER)) {
      this.emailService.sendInfoUserDetails(emailMessage.getMailDetails());
    } else if (emailMessage.getMessageType().equals(EmailType.CODE_VERIFY)) {
      this.emailService.sendVerificationEmail(emailMessage.getMailDetails());
    } else if (emailMessage.getMessageType().equals(EmailType.INVOICE_HOTEL)) {
      this.emailService.sendInvoiceHotelEmail(emailMessage.getContentInvoiceHotel());
    } else if (emailMessage.getMessageType().equals(EmailType.INVOICE_TOUR)) {
      this.emailService.sendInvoiceTourEmail(emailMessage.getContentInvoiceTour());
    } else if (emailMessage.getMessageType().equals(EmailType.CODE_FORGET_PASSWORD)) {
      this.emailService.sendVerificationResetPassword(emailMessage.getMailDetails());
    } else if (emailMessage.getMessageType().equals(EmailType.RESET_PASSWORD)) {
      this.emailService.sendResetPassword(emailMessage.getMailDetails());
    }
  }
}
