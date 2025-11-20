package com.bestzedcoder.project3.booking_tour_hotel.model;

import com.bestzedcoder.project3.booking_tour_hotel.common.BaseEntity;
import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingRoomType;
import com.bestzedcoder.project3.booking_tour_hotel.enums.HotelStar;
import com.bestzedcoder.project3.booking_tour_hotel.enums.RoomType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "_hotel_booking")
public class HotelBooking extends BaseEntity {
  @OneToOne
  @JoinColumn(name = "booking_id")
  private Booking booking;

  @Column(name = "hotel_name",nullable = false)
  private String hotelName;

  @Column(name = "hotel_star",nullable = false)
  @Enumerated(EnumType.STRING)
  private HotelStar hotelStar;

  @Column(name = "room_name",nullable = false)
  private String roomName;

  @Column(name = "room_type",nullable = false)
  @Enumerated(EnumType.STRING)
  private RoomType roomType;

  @Column(name = "hotel_address",nullable = false)
  private String address;

  @Column(name = "booking_type",nullable = false)
  @Enumerated(EnumType.STRING)
  private BookingRoomType bookingRoomType;

  @Column(nullable = false)
  private int duration;

  @ManyToOne
  @JoinColumn(name = "room_id")
  private Room room;

  @Column(nullable = false)
  private LocalDate checkIn;

  @Column(nullable = false)
  private LocalDate checkOut;
}
