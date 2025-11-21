package com.bestzedcoder.project3.booking_tour_hotel.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelAndRoomDetailsBooking {
  private Long hotelId;
  private Long roomId;
  private String roomName;
  private String hotelName;
  private String hotelCity;
  private String hotelAddress;
  private String hotelPhone;
  private Double pricePerHour;
  private Double pricePerDay;
}
