package com.bestzedcoder.project3.booking_tour_hotel.dto.response;

import com.bestzedcoder.project3.booking_tour_hotel.enums.HotelStar;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HotelResponse {
  private Long hotelId;
  private String hotelName;
  private String hotelCity;
  private String hotelDescription;
  private String hotelAddress;
  private String hotelPhone;
  private HotelStar hotelStar;
  private List<String> hotelImages;
  private List<RoomResponse> rooms;
}
