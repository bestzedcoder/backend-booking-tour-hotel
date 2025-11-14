package com.bestzedcoder.project3.booking_tour_hotel.controller;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserUpdatingProfile;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.service.IProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("profile")
@Tag(name = "Profile API", description = "Quản lý hồ sơ người dùng hiện tại")
public class ProfileController {
  private final IProfileService profileService;

  @PutMapping(consumes = {"multipart/form-data"})
  @Operation(
      summary = "Cập nhật hồ sơ người dùng",
      description = "Cập nhật thông tin cá nhân và ảnh đại diện. Cần JWT để truy cập.",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  public ResponseEntity<ApiResponse<?>> update(
      @RequestPart("data") @Valid UserUpdatingProfile userUpdatingProfile,
      @RequestPart(value = "image", required = false) MultipartFile image) {
    ApiResponse<?> response = this.profileService.update(userUpdatingProfile, image);
    return ResponseEntity.ok(response);
  }
}
