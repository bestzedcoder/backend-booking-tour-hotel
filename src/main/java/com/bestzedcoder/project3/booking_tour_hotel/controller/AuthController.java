package com.bestzedcoder.project3.booking_tour_hotel.controller;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.RefreshTokenReqest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.SignRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserSignupRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.VerifyRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.LoginResponse;
import com.bestzedcoder.project3.booking_tour_hotel.exception.UnauthorizedException;
import com.bestzedcoder.project3.booking_tour_hotel.service.IAuthService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
  private final IAuthService authService;
  @PostMapping("login")
  public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid SignRequest signRequest) throws UnauthorizedException {
    log.info("Login request: {}", signRequest);
    ApiResponse<LoginResponse> response = this.authService.login(signRequest.getUsername(), signRequest.getPassword());
    return ResponseEntity.ok(response);
  }

  @GetMapping("profile")
  public ResponseEntity<ApiResponse<?>> profile(Authentication authentication) {
    return ResponseEntity.ok(new ApiResponse<>(true , "success" ,authentication.getPrincipal()));
  }

  @PostMapping("register")
  public ResponseEntity<ApiResponse<?>> register(@RequestBody @Valid UserSignupRequest userSignupRequest) {
    log.info("Register request: {}", userSignupRequest);
    ApiResponse<?> response = this.authService.register(userSignupRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("verify")
  public ResponseEntity<ApiResponse<?>> verify(@RequestBody @Valid VerifyRequest verifyRequest) {
    ApiResponse<?> response = this.authService.verify(verifyRequest.getCode(),verifyRequest.getEmail());
    return ResponseEntity.ok(response);
  }

  @PostMapping("refresh")
  public ResponseEntity<ApiResponse<?>> refreshToken(@RequestBody RefreshTokenReqest refreshTokenReqest) {
    ApiResponse<?> response = this.authService.refresh(refreshTokenReqest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("logout")
  public ResponseEntity<ApiResponse<?>> logout(@RequestBody Map<String , String>  data ) {
    String access_token = data.get("access_token");
    ApiResponse<?> response = this.authService.logout(access_token);
    return ResponseEntity.ok(response);
  }
}
