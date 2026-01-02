package com.bestzedcoder.project3.booking_tour_hotel.service;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.ChangePasswordRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.RefreshTokenReqest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserSignupRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IAuthService {
  ApiResponse<?> login(String username, String password, HttpServletResponse res);
  ApiResponse<?> register(UserSignupRequest userSignupRequest);
  ApiResponse<?> verify(String code,String email);
  ApiResponse<?> refresh(RefreshTokenReqest refreshTokenReqest, HttpServletRequest req);
  ApiResponse<?> logout(HttpServletResponse res);
  ApiResponse<?> authProfile();
  ApiResponse<?> changePassword(ChangePasswordRequest changePasswordRequest);
  void forgetPassword(String email);
  void verifyResetPassword(String code, String email);
}
