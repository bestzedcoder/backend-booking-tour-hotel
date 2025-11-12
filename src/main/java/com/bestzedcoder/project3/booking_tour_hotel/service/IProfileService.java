package com.bestzedcoder.project3.booking_tour_hotel.service;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserUpdatingProfile;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

public interface IProfileService {
  ApiResponse<?> update(UserUpdatingProfile userUpdatingProfile,
      MultipartFile multipartFile);
}
