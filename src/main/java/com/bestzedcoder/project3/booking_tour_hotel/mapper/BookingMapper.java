package com.bestzedcoder.project3.booking_tour_hotel.mapper;

import com.bestzedcoder.project3.booking_tour_hotel.dto.response.BookingSearchResponse;
import com.bestzedcoder.project3.booking_tour_hotel.model.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {
  public static BookingSearchResponse bookingToBookingSearchResponse(Booking booking) {
    BookingSearchResponse bookingSearchResponse = new BookingSearchResponse();
    bookingSearchResponse.setBookingId(booking.getId());
    bookingSearchResponse.setCode(booking.getBookingCode());
    bookingSearchResponse.setStatus(booking.getStatus());
    bookingSearchResponse.setPaymentMethod(booking.getPaymentMethod());
    bookingSearchResponse.setPrice(booking.getTotalPrice());
    return bookingSearchResponse;
  }
}
