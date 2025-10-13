package com.bestzedcoder.project3.booking_tour_hotel.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;



@Getter
public class UserSignupRequest {
  @NotBlank(message = "Username cannot be blank")
  private String username;

  @NotBlank(message = "Password cannot be blank")
  private String password;

  @NotBlank(message = "Email cannot be blank")
  @Email(message = "Email is not valid")
  private String email;
}
