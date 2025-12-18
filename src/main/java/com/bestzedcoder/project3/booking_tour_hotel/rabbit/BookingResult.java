package com.bestzedcoder.project3.booking_tour_hotel.rabbit;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingResult {
  private String bookingCode;
  private String status;
  private String failureReason;
}
