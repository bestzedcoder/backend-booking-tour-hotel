package com.bestzedcoder.project3.booking_tour_hotel.dto.response;


import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourDetailsBooking {
  private Long tourId;
  private String tourName;
  private String tourCity;
  private LocalDate startDate;
  private LocalDate endDate;
  private int duration;
  private Double price;
}
