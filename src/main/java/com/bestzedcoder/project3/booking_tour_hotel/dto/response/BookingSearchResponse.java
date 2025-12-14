package com.bestzedcoder.project3.booking_tour_hotel.dto.response;

import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingStatus;
import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingType;
import com.bestzedcoder.project3.booking_tour_hotel.enums.PaymentMethod;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingSearchResponse {
  private Long bookingId;
  private PaymentMethod paymentMethod;
  private BookingStatus status;
  private String code;
  private Double price;
  private BookingType type;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
