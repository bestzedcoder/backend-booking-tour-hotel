package com.bestzedcoder.project3.booking_tour_hotel.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ForgetPasswordRequest {
  @NotBlank(message = "email không thể để trống.")
  @Email(message = "không đúng định dạng email.")
  private String email;
}