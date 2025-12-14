package com.bestzedcoder.project3.booking_tour_hotel.mapper;

import com.bestzedcoder.project3.booking_tour_hotel.common.GenCode;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.BookingCustomerResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.BookingSearchResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.HotelBookingResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.TourBookingResponse;
import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingStatus;
import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingType;
import com.bestzedcoder.project3.booking_tour_hotel.enums.PaymentMethod;
import com.bestzedcoder.project3.booking_tour_hotel.model.Booking;
import com.bestzedcoder.project3.booking_tour_hotel.model.HotelBooking;
import com.bestzedcoder.project3.booking_tour_hotel.model.TourBooking;
import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import com.bestzedcoder.project3.booking_tour_hotel.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingMapper {
  private final BookingRepository bookingRepository;
  public  Booking createBooking(PaymentMethod method, BookingType type, User user) {
    Booking booking = new Booking();
    booking.setPaymentMethod(method);
    booking.setBookingCode(GenCode.generateOrderCode());
    booking.setStatus(BookingStatus.PENDING);
    booking.setBookingType(type);
    booking.setUser(user);
    booking.setTotalPrice(Double.valueOf(0.0));
    booking.setOwner(user.getId());
    booking = this.bookingRepository.save(booking);
    return booking;
  }

  public static BookingSearchResponse bookingToBookingSearchResponse(Booking booking) {
    BookingSearchResponse bookingSearchResponse = new BookingSearchResponse();
    bookingSearchResponse.setBookingId(booking.getId());
    bookingSearchResponse.setCode(booking.getBookingCode());
    bookingSearchResponse.setStatus(booking.getStatus());
    bookingSearchResponse.setPaymentMethod(booking.getPaymentMethod());
    bookingSearchResponse.setPrice(booking.getTotalPrice());
    bookingSearchResponse.setType(booking.getBookingType());
    bookingSearchResponse.setCreatedAt(booking.getCreatedAt());
    bookingSearchResponse.setUpdatedAt(booking.getUpdatedAt());
    return bookingSearchResponse;
  }

  public static BookingCustomerResponse<HotelBookingResponse> toHotelBookingResponse(Booking booking, HotelBooking hotelBooking) {
    HotelBookingResponse details = hotelBooking != null ? HotelBookingResponse.builder()
        .hotelName(hotelBooking.getHotelName())
        .hotelStar(hotelBooking.getHotelStar())
        .bookingRoomType(hotelBooking.getBookingRoomType())
        .hotelAddress(hotelBooking.getAddress())
        .checkIn(hotelBooking.getCheckIn())
        .checkOut(hotelBooking.getCheckOut())
        .duration(hotelBooking.getDuration())
        .roomName(hotelBooking.getRoomName())
        .roomType(hotelBooking.getRoomType())
        .build() : null;

    return BookingCustomerResponse.<HotelBookingResponse>builder()
        .bookingId(booking.getId())
        .bookingCode(booking.getBookingCode())
        .status(booking.getStatus())
        .paymentMethod(booking.getPaymentMethod())
        .price(booking.getTotalPrice())
        .type(booking.getBookingType())
        .createdAt(booking.getCreatedAt())
        .updatedAt(booking.getUpdatedAt())
        .details(details)
        .build();
  }

  public static BookingCustomerResponse<TourBookingResponse> toTourBookingResponse(Booking booking, TourBooking tourBooking) {
    TourBookingResponse details = tourBooking != null ? TourBookingResponse.builder()
        .tourName(tourBooking.getTourName())
        .people(tourBooking.getPeople())
        .duration(tourBooking.getDuration())
        .startDate(tourBooking.getStartDate())
        .endDate(tourBooking.getEndDate())
        .build() : null;

    return BookingCustomerResponse.<TourBookingResponse>builder()
        .bookingId(booking.getId())
        .bookingCode(booking.getBookingCode())
        .status(booking.getStatus())
        .paymentMethod(booking.getPaymentMethod())
        .price(booking.getTotalPrice())
        .type(booking.getBookingType())
        .createdAt(booking.getCreatedAt())
        .updatedAt(booking.getUpdatedAt())
        .details(details)
        .build();
  }
}
