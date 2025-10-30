package com.bestzedcoder.project3.booking_tour_hotel.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserUpdatingProfile {
  @NotBlank(message = "Số điện thoại không được để trống")
  @Pattern(
      regexp = "^(0|\\+84)(\\d{9})$",
      message = "Số điện thoại không hợp lệ (phải có 10 chữ số, bắt đầu bằng 0 hoặc +84)"
  )
  private String phone;

  @NotBlank(message = "Địa chỉ không được để trống")
  private String address;

  @NotBlank(message = "Họ tên không được để trống")
  @Size(max = 50, message = "Họ tên không được vượt quá 50 ký tự")
  private String fullName;
}
