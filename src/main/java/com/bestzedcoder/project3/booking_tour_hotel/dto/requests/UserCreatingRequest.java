package com.bestzedcoder.project3.booking_tour_hotel.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class UserCreatingRequest {
  @NotBlank(message = "Username không được để trống")
  @Size(min = 4, max = 30, message = "Username phải có độ dài từ 4 đến 30 ký tự")
  private String username;

  @NotBlank(message = "Email không được để trống")
  @Email(message = "Email không hợp lệ")
  private String email;

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

  @Size(min = 1, message = "Phải có ít nhất 1 vai trò (role)")
  private String[] roles;
}
