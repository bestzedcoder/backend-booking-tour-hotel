package com.bestzedcoder.project3.booking_tour_hotel.controller;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.BookingHotelRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.service.IBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("bookings")
@RequiredArgsConstructor
public class BookingController {
  private final IBookingService bookingService;

  @PostMapping("/hotel/{hotelId}/room/{roomId}")
  public ResponseEntity<ApiResponse<?>> bookingHotel(@RequestBody BookingHotelRequest bookingHotelRequest, @PathVariable Long hotelId, @PathVariable Long roomId) {
    ApiResponse<?> response = this.bookingService.bookingHotel(bookingHotelRequest , hotelId , roomId);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

//  @GetMapping
//  public ResponseEntity<>

//  @PostMapping("/tour/{tourId}")
//  public ResponseEntity<ApiResponse<?>> bookingTour() {}
}
