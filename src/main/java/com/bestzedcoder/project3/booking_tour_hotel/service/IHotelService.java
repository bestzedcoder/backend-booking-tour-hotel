package com.bestzedcoder.project3.booking_tour_hotel.service;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.HotelCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

public interface IHotelService {
  ApiResponse<?> create(HotelCreatingRequest hotelCreatingRequest , MultipartFile[] images);
  ApiResponse<?> getHotelsByOwnerId(Long ownerId);
}
