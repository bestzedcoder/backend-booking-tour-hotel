package com.bestzedcoder.project3.booking_tour_hotel.dto.response;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TourBookingResponse {
  private String tourName;
  private LocalDate startDate;
  private LocalDate endDate;
  private int people;
  private int duration;
}
