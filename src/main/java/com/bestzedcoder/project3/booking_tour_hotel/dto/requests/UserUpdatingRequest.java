package com.bestzedcoder.project3.booking_tour_hotel.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserUpdatingRequest {
  @NotBlank(message = "fullname cannot be blank")
  private String fullName;
  @NotBlank(message = "Phone number cannot be blank")
  private String phone;
  private String address;
  private String[] roles;
}
