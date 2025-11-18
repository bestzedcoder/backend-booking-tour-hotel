package com.bestzedcoder.project3.booking_tour_hotel.service;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.BookingHotelRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.BookingTourRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;

public interface IBookingService {
  ApiResponse<?> bookingHotel(BookingHotelRequest request , Long hotelId , Long roomId);
  ApiResponse<?> bookingTour(BookingTourRequest request, Long tourId);
}
