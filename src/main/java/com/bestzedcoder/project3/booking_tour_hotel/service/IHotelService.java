package com.bestzedcoder.project3.booking_tour_hotel.service;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.HotelCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.RoomsCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.PageResponse;
import com.bestzedcoder.project3.booking_tour_hotel.enums.HotelStar;
import org.springframework.web.multipart.MultipartFile;

public interface IHotelService {
  ApiResponse<?> create(HotelCreatingRequest hotelCreatingRequest , MultipartFile[] images);
  ApiResponse<?> getHotelsByOwnerId(Long ownerId);
  ApiResponse<?> createRoom(Long hotelId, RoomsCreatingRequest roomsCreatingRequest);
  PageResponse<?> searchByUser(int page, int limit, String hotelName, String address, String city ,
      HotelStar hotelStar);
}
