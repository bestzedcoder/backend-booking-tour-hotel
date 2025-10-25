package com.bestzedcoder.project3.booking_tour_hotel.service;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserUpdatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import org.apache.coyote.BadRequestException;

public interface IUserService {
  ApiResponse<?> create(UserCreatingRequest request) throws BadRequestException;
  ApiResponse<?> getUserById(Long id) throws BadRequestException;
  ApiResponse<?> updateUserById(Long id, UserUpdatingRequest request) throws BadRequestException;
  ApiResponse<?> getAllUsers();
}
