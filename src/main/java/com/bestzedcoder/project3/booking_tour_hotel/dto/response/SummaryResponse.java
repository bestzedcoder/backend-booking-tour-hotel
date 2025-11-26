package com.bestzedcoder.project3.booking_tour_hotel.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SummaryResponse {
  private Long totalUsers;
  private Long totalTours;
  private Long totalHotels;
  private Long totalBookings;
}
