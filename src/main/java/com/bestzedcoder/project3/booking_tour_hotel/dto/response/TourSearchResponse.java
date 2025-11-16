package com.bestzedcoder.project3.booking_tour_hotel.dto.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TourSearchResponse {
  private Long tourId;
  private String tourName;
  private String tourDescription;
  private String tourCity;
  private LocalDate tourStart;
  private LocalDate tourEnd;
  private Double tourPrice;
  private int tourDuration;
  private String tourImageUrl;
  private int tourMaxPeople;
}
