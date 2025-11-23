package com.bestzedcoder.project3.booking_tour_hotel.service;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.BookingHotelRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.BookingTourRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.PageResponse;
import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingStatus;
import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingType;
import com.bestzedcoder.project3.booking_tour_hotel.enums.PaymentMethod;

public interface IBookingService {
  ApiResponse<?> bookingHotel(BookingHotelRequest request , Long hotelId , Long roomId);
  ApiResponse<?> bookingTour(BookingTourRequest request, Long tourId);
  ApiResponse<?> updateStatus(Long id , BookingStatus status);
  ApiResponse<?> getByAdminV2(String name);
  ApiResponse<?> deleteBooking(Long id);
  ApiResponse<?> invoice(Long id);
  PageResponse<?> getByAdminV1(int page, int limit, BookingStatus status, String code, String username , PaymentMethod method , BookingType type);
  ApiResponse<?> getByCustomer();
}
