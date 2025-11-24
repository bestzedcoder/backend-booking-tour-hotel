package com.bestzedcoder.project3.booking_tour_hotel.dto.requests;

import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingStatus;
import lombok.Getter;

@Getter
public class BookingUpdatingRequest {
  private BookingStatus status;
}
