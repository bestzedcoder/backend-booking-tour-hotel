package com.bestzedcoder.project3.booking_tour_hotel.controller;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserUpdatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.UserCreatingResponse;
import com.bestzedcoder.project3.booking_tour_hotel.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
  private final IUserService userService;

  @PostMapping("/create")
  public ResponseEntity<ApiResponse<UserCreatingResponse>> create(@RequestBody @Valid
      UserCreatingRequest request) throws BadRequestException {
      log.info("Creating user: {}", request);
      ApiResponse<UserCreatingResponse> response = this.userService.create(request);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<?>> findById(@PathVariable("id") Long id) throws BadRequestException {
    log.info("Find user by id: {}", id);
    ApiResponse<?> response = this.userService.getUserById(id);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PutMapping("/update/{id}")
  public  ResponseEntity<ApiResponse<?>> update(@PathVariable("id") Long id ,@RequestBody @Valid UserUpdatingRequest request) throws BadRequestException {
    log.info("Updating user: {}", request);
    ApiResponse<?> response = this.userService.updateUserById(id,request);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
