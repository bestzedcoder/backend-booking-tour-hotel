package com.bestzedcoder.project3.booking_tour_hotel.mail;

import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingRoomType;
import com.bestzedcoder.project3.booking_tour_hotel.exception.BadRequestException;
import com.bestzedcoder.project3.booking_tour_hotel.service.iml.MacService;
import com.bestzedcoder.project3.booking_tour_hotel.service.iml.QRCodeService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
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
  private final MacService macService;
  private final QRCodeService qrCodeService;

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
    String macDataToEncode = String.format("%s|%f|%s|%s",
        contentInvoiceHotel.getBookingCode(),
        contentInvoiceHotel.getTotalPrice(),
        contentInvoiceHotel.getCheckIn(),
        contentInvoiceHotel.getStatus());
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
      // Sinh ảnh QR (dùng 150x150 pixels, định dạng PNG hoặc JPEG)
      // Giả sử service này trả về mảng byte[] PNG
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
      //  Nhúng ảnh QR Code vào email bằng CID
      // "image/png" phải khớp với định dạng ảnh bạn sinh ra (Nếu là JPEG thì dùng "image/jpeg")
      helper.addInline(QR_CID, new ByteArrayResource(qrCodeImageBytes), "image/png");
      mailSender.send(message);
    } catch (MessagingException e) {
      throw new RuntimeException("Gửi email thất bại", e);
    }
  }

  @Override
  public void sendInvoiceTourEmail(ContentInvoiceTour contentInvoiceTour) {

  }
}
