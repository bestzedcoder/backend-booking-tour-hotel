package com.bestzedcoder.project3.booking_tour_hotel.service;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.ChangePasswordRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.RefreshTokenReqest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserSignupRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.LoginResponse;
import java.util.Map;

public interface IAuthService {
  ApiResponse<LoginResponse> login(String username, String password);
  ApiResponse<?> register(UserSignupRequest userSignupRequest);
  ApiResponse<?> verify(String code,String email);
  ApiResponse<?> refresh(RefreshTokenReqest refreshTokenReqest);
  ApiResponse<?> logout();
  ApiResponse<?> authProfile();
  ApiResponse<?> changePassword(ChangePasswordRequest changePasswordRequest);
}
