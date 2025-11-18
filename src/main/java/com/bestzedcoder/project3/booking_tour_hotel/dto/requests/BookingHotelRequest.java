package com.bestzedcoder.project3.booking_tour_hotel.dto.requests;

import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingRoomType;
import com.bestzedcoder.project3.booking_tour_hotel.enums.PaymentMethod;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class BookingHotelRequest {
  private BookingRoomType bookingType;
  private PaymentMethod paymentMethod;
  private int duration;
  private Double totalPrice;
  private LocalDate checkIn;
  private LocalDate checkOut;
}
