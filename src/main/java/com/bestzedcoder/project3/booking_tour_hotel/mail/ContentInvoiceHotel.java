package com.bestzedcoder.project3.booking_tour_hotel.mail;

import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingRoomType;
import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingStatus;
import com.bestzedcoder.project3.booking_tour_hotel.enums.HotelStar;
import com.bestzedcoder.project3.booking_tour_hotel.enums.PaymentMethod;
import com.bestzedcoder.project3.booking_tour_hotel.enums.RoomType;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContentInvoiceHotel {
  private String to;
  private String bookingCode;
  private String hotelName;
  private String hotelAddress;
  private HotelStar hotelStar;
  private String roomName;
  private RoomType roomType;
  private BookingStatus status;
  private LocalDate checkIn;
  private LocalDate checkOut;
  private int duration;
  private PaymentMethod paymentMethod;
  private Double totalPrice;
  private BookingRoomType bookingRoomType;
}
