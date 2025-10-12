package com.bestzedcoder.project3.booking_tour_hotel.service;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.UserCreatingResponse;
import org.apache.coyote.BadRequestException;

public interface IUserService {
  ApiResponse<UserCreatingResponse> create(UserCreatingRequest request) throws BadRequestException;
}
