package com.bestzedcoder.project3.booking_tour_hotel.rabbit;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingResult {
  private Long bookingId;
  private String status;
  private String failureReason;
}
