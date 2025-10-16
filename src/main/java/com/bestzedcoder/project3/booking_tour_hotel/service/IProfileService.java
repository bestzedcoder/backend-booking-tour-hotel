package com.bestzedcoder.project3.booking_tour_hotel.service;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserUpdatingProfile;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;

public interface IProfileService {
  ApiResponse<?> update(Long id, UserUpdatingProfile userUpdatingProfile);
}
