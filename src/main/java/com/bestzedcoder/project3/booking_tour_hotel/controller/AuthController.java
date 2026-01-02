package com.bestzedcoder.project3.booking_tour_hotel.controller;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.*;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.exception.UnauthorizedException;
import com.bestzedcoder.project3.booking_tour_hotel.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Auth API", description = "Quản lý xác thực và đăng ký người dùng")
public class AuthController {
  private final IAuthService authService;

  @PostMapping("login")
  @Operation(summary = "Đăng nhập", description = "Trả về accessToken và refreshToken")
  public ResponseEntity<ApiResponse<?>> login(@RequestBody @Valid SignRequest signRequest,@NotNull
      HttpServletResponse res) throws UnauthorizedException {
    log.info("Login request: {}", signRequest);
    ApiResponse<?> response = this.authService.login(signRequest.getUsername(), signRequest.getPassword(), res);
    return ResponseEntity.ok(response);
  }

  @PostMapping("register")
  @Operation(summary = "Đăng ký người dùng mới")
  public ResponseEntity<ApiResponse<?>> register(@RequestBody @Valid UserSignupRequest userSignupRequest) {
    log.info("Register request: {}", userSignupRequest);
    ApiResponse<?> response = this.authService.register(userSignupRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("verify")
  @Operation(summary = "Xác thực email người dùng")
  public ResponseEntity<ApiResponse<?>> verify(@RequestBody @Valid VerifyRequest verifyRequest) {
    ApiResponse<?> response = this.authService.verify(verifyRequest.getCode(), verifyRequest.getEmail());
    return ResponseEntity.ok(response);
  }

  @GetMapping("profile")
  @Operation(summary = "Lấy thông tin người dùng hiện tại", description = "Cần JWT để truy cập")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<ApiResponse<?>> profile() {
    ApiResponse<?> response = this.authService.authProfile();
    return ResponseEntity.ok(response);
  }

  @PostMapping("refresh")
  @Operation(summary = "Làm mới access token bằng refresh token", description = "Cần JWT nếu refresh token đã được gửi")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<ApiResponse<?>> refreshToken(@RequestBody RefreshTokenReqest refreshTokenReqest, @NotNull
      HttpServletRequest req) {
    ApiResponse<?> response = this.authService.refresh(refreshTokenReqest,req);
    return ResponseEntity.ok(response);
  }

  @GetMapping("logout")
  @Operation(summary = "Đăng xuất người dùng hiện tại", description = "Cần JWT để logout")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<ApiResponse<?>> logout(@NotNull HttpServletResponse res) {
    ApiResponse<?> response = this.authService.logout(res);
    return ResponseEntity.ok(response);
  }

  @PostMapping("change-password")
  @Operation(summary = "Đổi mật khẩu người dùng", description = "Cần JWT để đổi mật khẩu")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<ApiResponse<?>> changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
    ApiResponse<?> response = this.authService.changePassword(changePasswordRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("forget-password")
  @Operation(summary = "Quên mật khẩu", description = "Gửi yêu cầu khôi phục mật khẩu tới email của người dùng.")
  public ResponseEntity<ApiResponse<?>> forgetPassword(@RequestBody @Valid ForgetPasswordRequest request) {
    this.authService.forgetPassword(request.getEmail());
    return ResponseEntity.ok(
        ApiResponse.builder()
            .success(true)
            .message("Hãy nhập mã xác thực được gửi về mail.")
            .build()
    );
  }

  @PostMapping("reset-password")
  @Operation(summary = "Xác nhận đặt lại mật khẩu", description = "Xác nhận mã code từ email để hoàn tất quá trình đặt lại mật khẩu.")
  public ResponseEntity<ApiResponse<?>> resetPassword(
      @RequestBody @Valid ResetPasswordRequest request  ) {
    this.authService.verifyResetPassword(request.getCode(), request.getEmail());
    return ResponseEntity.ok(
        ApiResponse.builder()
            .success(true)
            .message("Mật khẩu đã được thiết lập lại vui lòng vào mail kiểm tra.")
            .build()
    );
  }
}
