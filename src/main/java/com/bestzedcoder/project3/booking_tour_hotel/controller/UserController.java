package com.bestzedcoder.project3.booking_tour_hotel.controller;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserUpdatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.PageResponse;
import com.bestzedcoder.project3.booking_tour_hotel.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User API", description = "Quản lý người dùng")
@SecurityRequirement(name = "bearerAuth")  // JWT cho tất cả endpoint
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
  private final IUserService userService;

  @PostMapping
  @Operation(summary = "Tạo người dùng mới")
  public ResponseEntity<ApiResponse<?>> create(@RequestBody @Valid UserCreatingRequest request) throws BadRequestException {
    log.info("Creating user: {}", request);
    ApiResponse<?> response = this.userService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy thông tin người dùng theo ID")
  public ResponseEntity<ApiResponse<?>> findById(@PathVariable("id") Long id) {
    log.info("Find user by id: {}", id);
    ApiResponse<?> response = this.userService.getUserById(id);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping
  @Operation(summary = "Lấy danh sách tất cả người dùng")
  public ResponseEntity<PageResponse<?>> getAllUsers(@RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit) {
    PageResponse<?> response = this.userService.getAllUsers(page, limit);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
  @Operation(summary = "Cập nhật thông tin người dùng")
  public ResponseEntity<ApiResponse<?>> update(@PathVariable("id") Long id,
      @RequestPart("data") @Valid UserUpdatingRequest request,
      @RequestPart(value = "image", required = false) MultipartFile image) {
    log.info("Updating user: {}", request);
    ApiResponse<?> response = this.userService.updateUserById(id, request, image);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Xóa người dùng theo ID")
  public ResponseEntity<ApiResponse<?>> delete(@PathVariable("id") Long id) {
    log.info("Deleting user: {}", id);
    ApiResponse<?> response = this.userService.deleteUserById(id);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
