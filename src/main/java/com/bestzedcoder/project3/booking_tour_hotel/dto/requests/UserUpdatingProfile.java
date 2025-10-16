package com.bestzedcoder.project3.booking_tour_hotel.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserUpdatingProfile {
  @NotBlank(message = "FullName cannot be blank")
  private String fullName;
  @NotBlank(message = "Phone cannot be blank")
  private String phoneNumber;

  private String address;
}
