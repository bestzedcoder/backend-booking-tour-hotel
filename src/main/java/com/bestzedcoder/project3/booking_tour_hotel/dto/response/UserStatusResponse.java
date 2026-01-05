package com.bestzedcoder.project3.booking_tour_hotel.dto.response;

import lombok.Data;

@Data
public class UserStatusResponse {
  private Long userId;
  private boolean online;
  private String fullName;
  private String imageUrl;
}
