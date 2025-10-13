package com.bestzedcoder.project3.booking_tour_hotel.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserCreatingRequest {
  @NotBlank(message = "Username cannot be blank")
  private String username;

  @NotBlank(message = "Password cannot be blank")
  private String password;

  // fullName có thể null hoặc rỗng → sẽ được set mặc định là "Ẩn danh"
  private String fullName = "anonymous";

  @NotBlank(message = "Email cannot be blank")
  @Email(message = "Email is not valid")
  private String email;

  @NotBlank(message = "Phone number cannot be blank")
  private String phone;

}
