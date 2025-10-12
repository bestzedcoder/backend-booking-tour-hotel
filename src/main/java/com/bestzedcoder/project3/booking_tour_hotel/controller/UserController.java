package com.bestzedcoder.project3.booking_tour_hotel.controller;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.UserCreatingResponse;
import com.bestzedcoder.project3.booking_tour_hotel.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
  private final IUserService userService;

  @PostMapping("/create")
  public ResponseEntity<ApiResponse<UserCreatingResponse>> create(@RequestBody @Valid
      UserCreatingRequest request) throws BadRequestException {
      log.info("Creating user: {}", request);
      ApiResponse<UserCreatingResponse> response = this.userService.create(request);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

}
