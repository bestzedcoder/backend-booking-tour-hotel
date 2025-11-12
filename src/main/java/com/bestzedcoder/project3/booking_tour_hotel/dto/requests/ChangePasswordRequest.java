package com.bestzedcoder.project3.booking_tour_hotel.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ChangePasswordRequest {
  @NotBlank(message = "oldPassword là bắt buộc")
  private String oldPassword;
  @NotBlank(message = "newPassword là bắt buộc")
  @Size(min = 8 , message = "newPassword phải có độ dài tối thiểu là 8 ký tự")
  private String newPassword;

}
