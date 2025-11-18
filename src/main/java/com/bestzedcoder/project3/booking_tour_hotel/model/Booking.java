package com.bestzedcoder.project3.booking_tour_hotel.model;

import com.bestzedcoder.project3.booking_tour_hotel.common.BaseEntity;
import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingStatus;
import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingType;
import com.bestzedcoder.project3.booking_tour_hotel.enums.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "_booking")
public class Booking extends BaseEntity {
  @Column(unique = true, nullable = false)
  private String booking_code;

  @Column(name = "booking_type",nullable = false)
  @Enumerated(EnumType.STRING)
  private BookingType bookingType;

  @Column(name = "booking_price")
  private Double totalPrice;

  @Column(name = "booking_status" ,nullable = false)
  @Enumerated(EnumType.STRING)
  private BookingStatus status;

  @Column(name = "payment_method",nullable = false)
  @Enumerated(EnumType.STRING)
  private PaymentMethod paymentMethod;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id")
  @JsonIgnore
  private User user;
}
