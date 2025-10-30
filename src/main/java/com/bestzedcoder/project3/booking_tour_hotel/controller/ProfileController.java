package com.bestzedcoder.project3.booking_tour_hotel.controller;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserUpdatingProfile;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.service.IProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("profile")
public class ProfileController {
  private final IProfileService profileService;

  @PutMapping(value = "/{id}" , consumes = {"multipart/form-data"})
  public ResponseEntity<ApiResponse<?>> update(@PathVariable("id") Long id,
                                               @RequestPart("data") UserUpdatingProfile userUpdatingProfile,
                                               @RequestPart(value = "image" , required = false) MultipartFile image) {
    ApiResponse<?> response = this.profileService.update(id,userUpdatingProfile,image);
    return ResponseEntity.ok(response);
  }
}
