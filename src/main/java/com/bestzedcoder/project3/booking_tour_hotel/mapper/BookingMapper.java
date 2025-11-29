package com.bestzedcoder.project3.booking_tour_hotel.mapper;

import com.bestzedcoder.project3.booking_tour_hotel.dto.response.BookingCustomerResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.BookingSearchResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.HotelBookingResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.TourBookingResponse;
import com.bestzedcoder.project3.booking_tour_hotel.model.Booking;
import com.bestzedcoder.project3.booking_tour_hotel.model.HotelBooking;
import com.bestzedcoder.project3.booking_tour_hotel.model.TourBooking;
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
    bookingSearchResponse.setType(booking.getBookingType());
    return bookingSearchResponse;
  }

  public static BookingCustomerResponse<HotelBookingResponse> toHotelBookingResponse(Booking booking, HotelBooking hotelBooking) {
    HotelBookingResponse details = HotelBookingResponse.builder()
        .hotelName(hotelBooking.getHotelName())
        .hotelStar(hotelBooking.getHotelStar())
        .bookingRoomType(hotelBooking.getBookingRoomType())
        .hotelAddress(hotelBooking.getAddress())
        .checkIn(hotelBooking.getCheckIn())
        .checkOut(hotelBooking.getCheckOut())
        .duration(hotelBooking.getDuration())
        .roomName(hotelBooking.getRoomName())
        .roomType(hotelBooking.getRoomType())
        .build();

    return BookingCustomerResponse.<HotelBookingResponse>builder()
        .bookingId(booking.getId())
        .bookingCode(booking.getBookingCode())
        .status(booking.getStatus())
        .paymentMethod(booking.getPaymentMethod())
        .price(booking.getTotalPrice())
        .type(booking.getBookingType())
        .details(details)
        .build();
  }

  public static BookingCustomerResponse<TourBookingResponse> toTourBookingResponse(Booking booking, TourBooking tourBooking) {
    TourBookingResponse details = TourBookingResponse.builder()
        .tourName(tourBooking.getTourName())
        .people(tourBooking.getPeople())
        .duration(tourBooking.getDuration())
        .startDate(tourBooking.getStartDate())
        .endDate(tourBooking.getEndDate())
        .build();

    return BookingCustomerResponse.<TourBookingResponse>builder()
        .bookingId(booking.getId())
        .bookingCode(booking.getBookingCode())
        .status(booking.getStatus())
        .paymentMethod(booking.getPaymentMethod())
        .price(booking.getTotalPrice())
        .type(booking.getBookingType())
        .details(details)
        .build();
  }
}
