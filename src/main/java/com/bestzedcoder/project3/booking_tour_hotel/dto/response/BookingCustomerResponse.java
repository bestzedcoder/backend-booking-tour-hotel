package com.bestzedcoder.project3.booking_tour_hotel.dto.response;

import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingStatus;
import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingType;
import com.bestzedcoder.project3.booking_tour_hotel.enums.PaymentMethod;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class  BookingCustomerResponse <T> {
  private Long bookingId;
  private BookingStatus status;
  private BookingType type;
  private String bookingCode;
  private Double price;
  private T details;
  private PaymentMethod paymentMethod;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
