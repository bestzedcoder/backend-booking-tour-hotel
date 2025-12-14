package com.bestzedcoder.project3.booking_tour_hotel.rabbit;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.BookingHotelRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.BookingTourRequest;
import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingMessage {

  private BookingType bookingType;
  private Long userId;

  // TOUR
  private Long tourId;

  // HOTEL
  private Long hotelId;
  private Long roomId;

  private BookingTourRequest tourRequest;
  private BookingHotelRequest hotelRequest;
}
