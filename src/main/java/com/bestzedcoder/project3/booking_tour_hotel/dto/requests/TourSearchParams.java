package com.bestzedcoder.project3.booking_tour_hotel.dto.requests;

import java.time.LocalDate;
import lombok.Data;

@Data
public class TourSearchParams {
  private String tourName ;
  private String tourCity ;

  private Double priceMin;
  private Double priceMax;

  private LocalDate startDate;
  private LocalDate endDate;

  private int page = 1;
  private int limit = 10;
}
