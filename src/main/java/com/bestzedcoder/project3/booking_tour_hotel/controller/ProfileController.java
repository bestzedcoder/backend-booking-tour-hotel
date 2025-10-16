package com.bestzedcoder.project3.booking_tour_hotel.controller;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserUpdatingProfile;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.service.IProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("profile")
public class ProfileController {
  private final IProfileService profileSerice;
  @PreAuthorize("hasRole('ROLE_CUSTOMER')")
  @PutMapping("update/{id}")
  public ResponseEntity<ApiResponse<?>> update(@PathVariable("id") Long id,@RequestBody UserUpdatingProfile userUpdatingProfile) {
    ApiResponse<?> response = this.profileSerice.update(id,userUpdatingProfile);
    return ResponseEntity.ok(response);
  }
}
