package com.bestzedcoder.project3.booking_tour_hotel.mail;

import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingRoomType;
import com.bestzedcoder.project3.booking_tour_hotel.rabbit.RabbitProducer;
import com.bestzedcoder.project3.booking_tour_hotel.service.iml.MacService;
import com.bestzedcoder.project3.booking_tour_hotel.service.iml.QRCodeService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class MailService implements IEmailService {
  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;
  private final MacService macService;
  private final QRCodeService qrCodeService;
  private final RabbitProducer rabbitProducer;

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

  @Override
  public void sendInvoiceHotelEmail(ContentInvoiceHotel contentInvoiceHotel) {
    Context context = new Context();
    context.setVariable("bookingCode" , contentInvoiceHotel.getBookingCode());
    context.setVariable("hotelName" , contentInvoiceHotel.getHotelName());
    context.setVariable("address" , contentInvoiceHotel.getHotelAddress());
    context.setVariable("roomType" , contentInvoiceHotel.getRoomType());
    context.setVariable("roomName" , contentInvoiceHotel.getRoomName());
    context.setVariable("hotelStar" , contentInvoiceHotel.getHotelStar());
    context.setVariable("status" , contentInvoiceHotel.getStatus());
    context.setVariable("checkIn" , contentInvoiceHotel.getCheckIn());
    context.setVariable("checkOut" , contentInvoiceHotel.getCheckOut());
    context.setVariable("duration" , contentInvoiceHotel.getBookingRoomType().equals(
        BookingRoomType.DAILY) ? contentInvoiceHotel.getDuration() + " ngày" : contentInvoiceHotel.getDuration() + " giờ");
    context.setVariable("paymentMethod" , contentInvoiceHotel.getPaymentMethod());
    context.setVariable("totalPrice" , contentInvoiceHotel.getTotalPrice());
    // gen mac
    String macDataToEncode = String.format("%s|%f|%s|%s|%s|%s",
        contentInvoiceHotel.getBookingCode(),
        contentInvoiceHotel.getTotalPrice(),
        contentInvoiceHotel.getCheckIn(),
        contentInvoiceHotel.getDuration(),
        contentInvoiceHotel.getStatus(),
        contentInvoiceHotel.getPaymentMethod());
    String macHash;
    try {
      macHash = macService.generateHmac(macDataToEncode);
    } catch (Exception e) {
      throw new RuntimeException("Lỗi khi tạo MAC Hash", e);
    }

    String qrCodeData = contentInvoiceHotel.getBookingCode() + "|" + macHash;
    //gen QR
    byte[] qrCodeImageBytes;
    try {
      qrCodeImageBytes = qrCodeService.generateQRCodeImage(qrCodeData, 150, 150);
    } catch (Exception e) {
      throw new RuntimeException("Lỗi khi tạo ảnh QR Code", e);
    }
    context.setVariable("macData" , qrCodeData);
    final String QR_CID = "qr-image-id";
    context.setVariable("qrCodeImageSource" , "cid:" + QR_CID);

    String htmlContent = templateEngine.process("invoice_hotel", context);
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setTo(contentInvoiceHotel.getTo());
      helper.setSubject("Hóa đơn đặt phòng của bạn: " + contentInvoiceHotel.getBookingCode());
      helper.setText(htmlContent, true);
      helper.addInline(QR_CID, new ByteArrayResource(qrCodeImageBytes), "image/png");
      mailSender.send(message);
    } catch (MessagingException e) {
      throw new RuntimeException("Gửi email thất bại", e);
    }
  }

  @Override
  public void sendInvoiceTourEmail(ContentInvoiceTour contentInvoiceTour) {
    Context context = new Context();
    context.setVariable("bookingCode" , contentInvoiceTour.getBookingCode());
    context.setVariable("tourName" , contentInvoiceTour.getTourName());
    context.setVariable("tourCity" , contentInvoiceTour.getTourCity());
    context.setVariable("duration" , contentInvoiceTour.getDuration());
    context.setVariable("startDate" , contentInvoiceTour.getStartDate());
    context.setVariable("endDate" , contentInvoiceTour.getEndDate());
    context.setVariable("people" , contentInvoiceTour.getPeople());
    context.setVariable("status" , contentInvoiceTour.getStatus());
    context.setVariable("paymentMethod" , contentInvoiceTour.getPaymentMethod());
    context.setVariable("totalPrice" , contentInvoiceTour.getTotalPrice());
    // gen mac
    String macDataToEncode = String.format("%s|%f|%s|%s|%s|%s",
        contentInvoiceTour.getBookingCode(),
        contentInvoiceTour.getTotalPrice(),
        contentInvoiceTour.getStartDate(),
        contentInvoiceTour.getDuration(),
        contentInvoiceTour.getStatus(),
        contentInvoiceTour.getPaymentMethod());

    String macHash;
    try {
      macHash = macService.generateHmac(macDataToEncode);
    } catch (Exception e) {
      throw new RuntimeException("Lỗi khi tạo MAC Hash", e);
    }

    String qrCodeData = contentInvoiceTour.getBookingCode() + "|" + macHash;
    //gen QR
    byte[] qrCodeImageBytes;
    try {
      qrCodeImageBytes = qrCodeService.generateQRCodeImage(qrCodeData, 150, 150);
    } catch (Exception e) {
      throw new RuntimeException("Lỗi khi tạo ảnh QR Code", e);
    }
    context.setVariable("macData" , qrCodeData);
    final String QR_CID = "qr-image-id";
    context.setVariable("qrCodeImageSource" , "cid:" + QR_CID);

    String htmlContent = templateEngine.process("invoice_tour", context);
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setTo(contentInvoiceTour.getTo());
      helper.setSubject("Hóa đơn đặt tour của bạn: " + contentInvoiceTour.getBookingCode());
      helper.setText(htmlContent, true);
      helper.addInline(QR_CID, new ByteArrayResource(qrCodeImageBytes), "image/png");
      mailSender.send(message);
    } catch (MessagingException e) {
      throw new RuntimeException("Gửi email thất bại", e);
    }
  }

  @Override
  public void sendVerificationResetPassword(MailDetails mailDetails) {
    Context context = new Context();
    context.setVariable("email", mailDetails.getTo());
    context.setVariable("code", mailDetails.getToken());

    String htmlContent = templateEngine.process("code-reset", context);

    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setTo(mailDetails.getTo());
      helper.setSubject("Xác thực để reset password");
      helper.setText(htmlContent, true);
      mailSender.send(message);
    } catch (MessagingException e) {
      throw new RuntimeException("Gửi email thất bại", e);
    }
  }

  @Override
  public void sendResetPassword(MailDetails mailDetails) {
    Context context = new Context();
    context.setVariable("email", mailDetails.getTo());
    context.setVariable("resetPassword", mailDetails.getRawPassword());
    String htmlContent = templateEngine.process("reset-password", context);

    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setTo(mailDetails.getTo());
      helper.setSubject("mật khẩu tài khoản của bạn");
      helper.setText(htmlContent, true);
      mailSender.send(message);
    } catch (MessagingException e) {
      throw new RuntimeException("Gửi email thất bại", e);
    }
  }
}
