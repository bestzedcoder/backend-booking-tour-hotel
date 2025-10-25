package com.bestzedcoder.project3.booking_tour_hotel.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
  private String username;
  private String fullName;
  private String address;
  private String email;
  private String phone;
  private String[] roles;
  private String urlImage;
}
