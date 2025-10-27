package com.bestzedcoder.project3.booking_tour_hotel.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RoomType {
  STANDARD, DELUXE, SUITE, FAMILY;
  @JsonCreator
  public static RoomType fromString(String value) {
    try {
      return RoomType.valueOf(value.toUpperCase());
    } catch (Exception e) {
      throw new IllegalArgumentException("Giá trị '" + value + "' không hợp lệ cho RoomType. Hợp lệ: STANDARD, DELUXE, SUITE, FAMILY");
    }
  }
}