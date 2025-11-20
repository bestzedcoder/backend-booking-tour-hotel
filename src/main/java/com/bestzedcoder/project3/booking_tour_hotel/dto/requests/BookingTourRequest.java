package com.bestzedcoder.project3.booking_tour_hotel.dto.requests;

import com.bestzedcoder.project3.booking_tour_hotel.enums.PaymentMethod;
import lombok.Getter;

@Getter
public class BookingTourRequest {
  private int people;
  private PaymentMethod paymentMethod;
}
