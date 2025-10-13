package com.bestzedcoder.project3.booking_tour_hotel.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class VerifyRequest {
  @NotBlank(message = "Email cannot be blank")
  @Email(message = "Email is not valid")
  private String email;

  @NotBlank(message = "Code cannot be blank")
  private String code;
}
