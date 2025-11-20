package com.bestzedcoder.project3.booking_tour_hotel.service.iml;

import com.bestzedcoder.project3.booking_tour_hotel.common.VNPayUtils;
import com.bestzedcoder.project3.booking_tour_hotel.config.VNPayConfig;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingStatus;
import com.bestzedcoder.project3.booking_tour_hotel.enums.PaymentMethod;
import com.bestzedcoder.project3.booking_tour_hotel.enums.PaymentStatus;
import com.bestzedcoder.project3.booking_tour_hotel.exception.BadRequestException;
import com.bestzedcoder.project3.booking_tour_hotel.exception.ResourceNotFoundException;
import com.bestzedcoder.project3.booking_tour_hotel.model.Booking;
import com.bestzedcoder.project3.booking_tour_hotel.model.Payment;
import com.bestzedcoder.project3.booking_tour_hotel.repository.BookingRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.PaymentRepository;
import com.bestzedcoder.project3.booking_tour_hotel.service.IPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {
  private final PaymentRepository paymentRepository;
  private final BookingRepository bookingRepository;
  private final VNPayConfig vnPayConfig;
  private final VNPayUtils vnPayUtils;
  @Value("${payment.vnPay.url}")
  private String vnpBaseUrl;
  @Override
  public ApiResponse<?> createPayment(Long bookingId, HttpServletRequest request) {
    Booking booking = this.bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    if(booking.getPaymentMethod().equals(PaymentMethod.CASH)) {
      throw new BadRequestException("Thanh toán bằng tiền mặt");
    }
    Payment payment = Payment.builder()
        .booking(booking)
        .amount(booking.getTotalPrice())
        .status(PaymentStatus.PENDING)
        .build();

    Map<String, String> params = vnPayConfig.getVNPayConfig(request);
    params.put("vnp_Amount", String.valueOf(payment.getAmount().intValue() * 100));
    params.put("vnp_OrderInfo", "Thanh toan don hang " + booking.getBookingCode());
    String paymentUrl = this.vnPayUtils.buildPaymentUrl(params, true);
    String hashData = this.vnPayUtils.buildPaymentUrl(params, false);
    String hashSecret = this.vnPayUtils.hmacSHA512(hashData);
    paymentUrl = this.vnpBaseUrl+ "?" + paymentUrl + "&vnp_SecureHash=" + hashSecret;
    payment.setTransactionNo(booking.getBookingCode());
    payment.setPaymentUrl(paymentUrl);
    paymentRepository.save(payment);
    return ApiResponse.builder()
        .success(true)
        .message("Payment created")
        .data(paymentUrl)
        .build();
  }

  @Override
  public ApiResponse<?> handleVNPayCallback(Map<String, String> vnPayResponse) {
    String txnNo = vnPayResponse.get("vnp_TransactionNo");
    String responseCode = vnPayResponse.get("vnp_ResponseCode");
    String info = vnPayResponse.get("vnp_OrderInfo");

    String[] parts = info.trim().split("\\s+");
    String bookingCode = parts[parts.length - 1];
    Payment payment = this.paymentRepository.findByTransactionNo(bookingCode)
        .orElseThrow(() -> new RuntimeException("Payment not found"));

    if ("00".equals(responseCode)) {
      payment.setStatus(PaymentStatus.SUCCESS);
      payment.getBooking().setStatus(BookingStatus.CONFIRMED);
    } else {
      payment.setStatus(PaymentStatus.FAILED);
      payment.getBooking().setStatus(BookingStatus.CANCELLED);
    }

    payment.setBankCode(vnPayResponse.get("vnp_BankCode"));
    payment.setPayDate(vnPayResponse.get("vnp_PayDate"));
    payment.setTransactionNo(txnNo);

    bookingRepository.save(payment.getBooking());
    paymentRepository.save(payment);
    return ApiResponse.builder()
        .success(true)
        .data(payment)
        .message(payment.getStatus().equals(PaymentStatus.SUCCESS) ? "Payment success" : "Payment failed")
        .build();
  }
}
