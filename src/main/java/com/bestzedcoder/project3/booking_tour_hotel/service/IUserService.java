package com.bestzedcoder.project3.booking_tour_hotel.service;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserUpdatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.PageResponse;
import java.util.Map;
import org.apache.coyote.BadRequestException;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService {
  ApiResponse<?> create(UserCreatingRequest request) throws BadRequestException;
  ApiResponse<?> getUserById(Long id);
  ApiResponse<?> updateUserById(Long id, UserUpdatingRequest request, MultipartFile image);
  PageResponse<?> getAllUsers(int page,int limit);
  ApiResponse<?> deleteUserById(Long id);
}
