package com.bestzedcoder.project3.booking_tour_hotel.service;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.HotelCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.HotelUpdatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.RoomUpdatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.RoomsCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.PageResponse;
import com.bestzedcoder.project3.booking_tour_hotel.enums.HotelStar;
import com.bestzedcoder.project3.booking_tour_hotel.enums.RoomStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

public interface IHotelService {
  ApiResponse<?> create(HotelCreatingRequest hotelCreatingRequest , MultipartFile[] images);
  PageResponse<?> getHotelsByOwner(int page, int limit, String hotelName, String city ,
      HotelStar hotelStar);
  ApiResponse<?> createRoom(Long hotelId, RoomsCreatingRequest roomsCreatingRequest);
  PageResponse<?> searchByUser(int page, int limit, String hotelName, String city ,
      HotelStar hotelStar);
  PageResponse<?> searchByAdmin(int page, int limit, String hotelName, String city ,
      HotelStar hotelStar,String owner);
  ApiResponse<?> getHotelById(Long hotelId);
  ApiResponse<?> deleteHotel(Long hotelId);
  ApiResponse<?> updateRoom(Long hotelId,Long roomId, RoomUpdatingRequest roomUpdatingRequest);
  ApiResponse<?> updateHotel(Long hotelId, HotelUpdatingRequest hotelUpdatingRequest, MultipartFile[] images);
  ApiResponse<?> infoHotelDetails(Long hotelId);
  ApiResponse<?> infoBooking(Long hotelId, Long roomId);
}
