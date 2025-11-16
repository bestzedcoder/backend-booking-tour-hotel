package com.bestzedcoder.project3.booking_tour_hotel.dto.requests;

import lombok.Data;

@Data
public class TourSearchByAdminParams {
  private int page = 1;
  private int limit = 10;
  private String tourName ;
  private String tourCity ;
  private String owner;
}
