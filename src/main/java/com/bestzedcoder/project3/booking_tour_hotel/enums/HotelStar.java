package com.bestzedcoder.project3.booking_tour_hotel.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum HotelStar {
  ONE_STAR, TWO_STAR, THREE_STAR, FOUR_STAR, FIVE_STAR;
  @JsonCreator
  public static HotelStar fromValue(String value) {
    try {
      return HotelStar.valueOf(value.toUpperCase());
    } catch (Exception e) {
      throw new IllegalArgumentException("Giá trị '" + value + "' không hợp lệ cho HotelStar. Hợp lệ: ONE_STAR, TWO_STAR, THREE_STAR, FOUR_STAR, FIVE_STAR");
    }
  }
}
