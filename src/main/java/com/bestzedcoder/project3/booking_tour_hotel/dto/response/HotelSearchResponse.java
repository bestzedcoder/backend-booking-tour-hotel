package com.bestzedcoder.project3.booking_tour_hotel.dto.response;

import com.bestzedcoder.project3.booking_tour_hotel.enums.HotelStar;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelSearchResponse {
  private String hotelName;
  private String address;
  private String city;
  private HotelStar star;
  private String description;
  private String imageUrl;
}
