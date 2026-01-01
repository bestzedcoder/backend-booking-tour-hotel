package com.bestzedcoder.project3.booking_tour_hotel.mail;

import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingStatus;
import com.bestzedcoder.project3.booking_tour_hotel.enums.PaymentMethod;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContentInvoiceTour {
  private String to;
  private String bookingCode;
  private String tourName;
  private String tourCity;
  private int people;
  private LocalDate startDate;
  private LocalDate endDate;
  private int duration;
  private Double totalPrice;
  private BookingStatus status;
  private PaymentMethod paymentMethod;
}
