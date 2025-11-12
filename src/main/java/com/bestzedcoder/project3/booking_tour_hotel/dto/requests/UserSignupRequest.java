package com.bestzedcoder.project3.booking_tour_hotel.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;



@Getter
public class UserSignupRequest {
  @NotBlank(message = "Username cannot be blank")
  private String username;


  @NotBlank(message = "Password là bắt buộc")
  @Size(min = 8 , message = "Password phải có độ dài tối thiểu là 8 ký tự")
  private String password;


  @NotBlank(message = "Email cannot be blank")
  @Email(message = "Email is not valid")
  private String email;
}
