package com.bestzedcoder.project3.booking_tour_hotel.dto.response;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class InfoTourDetails {
  private Long tourId;
  private String tourName;
  private String tourDescription;
  private String tourCity;
  private Double tourPrice;
  private LocalDate startDate;
  private LocalDate endDate;
  private int duration;
  private int maxPeople;
  private List<String> imageTourUrls;
  private List<TourScheduleResponse> tourSchedules;
  private InfoOwner owner;

  @Data
  public static class InfoOwner {
    private String phoneNumber;
    private String fullName;
    private String email;
    private String imageUrl;
  }
}
