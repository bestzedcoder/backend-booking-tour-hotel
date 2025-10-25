package com.bestzedcoder.project3.booking_tour_hotel.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HotelResponse {
  private String hotelName;
  private String hotelCity;
  private String hotelDescription;
  private String hotelAddress;
  private String hotelPhone;
  private String[] hotelImages;
}
