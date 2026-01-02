package com.bestzedcoder.project3.booking_tour_hotel.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {

  @NotBlank(message = "Email không được để trống")
  @Email(message = "Định dạng email không hợp lệ")
  private String email;

  @NotBlank(message = "Mã xác thực không được để trống")
  @Pattern(regexp = "^\\d{6}$", message = "Mã xác thực phải bao gồm đúng 6 chữ số")
  private String code;
}