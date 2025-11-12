package com.bestzedcoder.project3.booking_tour_hotel.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUserAllResponse {
  private Long id;
  private String username;
  private String fullName;
  private String email;
  private boolean active;
  private String[] roles;
}
