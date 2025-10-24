package com.bestzedcoder.project3.booking_tour_hotel.dto.response;

import com.bestzedcoder.project3.booking_tour_hotel.model.Profile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
  private Long userId;
  private String username;
  private Profile profile;
  private String access_token;
  private String refresh_token;
  private boolean updateProfile;
}
