package com.bestzedcoder.project3.booking_tour_hotel.service;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.HotelCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.RoomsCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.PageResponse;
import com.bestzedcoder.project3.booking_tour_hotel.enums.HotelStar;
import com.bestzedcoder.project3.booking_tour_hotel.enums.RoomStatus;
import org.springframework.web.multipart.MultipartFile;

public interface IHotelService {
  ApiResponse<?> create(HotelCreatingRequest hotelCreatingRequest , MultipartFile[] images);
  PageResponse<?> getHotelsByOwnerId(int page,int limit , Long ownerId);
  ApiResponse<?> createRoom(Long hotelId, RoomsCreatingRequest roomsCreatingRequest);
  PageResponse<?> searchByUser(int page, int limit, String hotelName, String address, String city ,
      HotelStar hotelStar);
  ApiResponse<?> deleteHotel(Long hotelId);
  ApiResponse<?> updateStatusRoom(Long hotelId,String roomName,RoomStatus roomStatus);
}
