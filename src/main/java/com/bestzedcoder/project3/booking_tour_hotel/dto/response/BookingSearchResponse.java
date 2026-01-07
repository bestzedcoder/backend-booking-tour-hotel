package com.bestzedcoder.project3.booking_tour_hotel.dto.response;

import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingRoomType;
import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingStatus;
import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingType;
import com.bestzedcoder.project3.booking_tour_hotel.enums.PaymentMethod;
import com.bestzedcoder.project3.booking_tour_hotel.enums.RoomType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingSearchResponse {
  private Long bookingId;
  private PaymentMethod paymentMethod;
  private BookingStatus status;
  private String code;
  private Double price;
  private BookingType type;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private InfoCustomer customer;
  private InfoDetails details;

  @Data
  public static class InfoCustomer {
    private String fullName;
    private String phoneNumber;
    private String email;
  }

  @Data
  public static class InfoDetails {
    private String name;
    private int duration;

    // tour
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int people;

    // hotel
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private RoomType roomType;
    private String roomName;
    private BookingRoomType bookingRoomType;
  }
}
