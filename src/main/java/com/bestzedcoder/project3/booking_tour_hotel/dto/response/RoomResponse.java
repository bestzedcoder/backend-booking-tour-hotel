package com.bestzedcoder.project3.booking_tour_hotel.dto.response;

import com.bestzedcoder.project3.booking_tour_hotel.enums.RoomStatus;
import com.bestzedcoder.project3.booking_tour_hotel.enums.RoomType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponse {
  private String roomName;
  private Double pricePerHour;
  private RoomType roomType;
  private Double pricePerDay;
  private RoomStatus status;
}
