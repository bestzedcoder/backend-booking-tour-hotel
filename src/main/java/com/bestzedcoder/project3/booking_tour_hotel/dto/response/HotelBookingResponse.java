package com.bestzedcoder.project3.booking_tour_hotel.dto.response;

import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingRoomType;
import com.bestzedcoder.project3.booking_tour_hotel.enums.HotelStar;
import com.bestzedcoder.project3.booking_tour_hotel.enums.RoomType;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class HotelBookingResponse {
  private String hotelName;
  private String hotelAddress;
  private String roomName;
  private RoomType roomType;
  private HotelStar hotelStar;
  private LocalDate checkIn;
  private LocalDate checkOut;
  private int duration;
  private BookingRoomType bookingRoomType;
}
