package com.bestzedcoder.project3.booking_tour_hotel.dto.response;

import com.bestzedcoder.project3.booking_tour_hotel.enums.HotelStar;
import lombok.Data;

@Data
public class InfoHotelDetails {
  private Long hotelId;
  private String hotelName;
  private String hotelCity;
  private String hotelDescription;
  private String hotelAddress;
  private String hotelPhone;
  private HotelStar hotelStar;
  private String[] hotelImages;
  private RoomResponse[] rooms;
  private InfoOwner owner;

  @Data
  public static class InfoOwner {
    private String phoneNumber;
    private String fullName;
  }
}
