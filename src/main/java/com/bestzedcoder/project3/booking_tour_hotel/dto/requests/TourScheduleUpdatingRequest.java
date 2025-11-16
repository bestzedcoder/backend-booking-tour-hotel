package com.bestzedcoder.project3.booking_tour_hotel.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TourScheduleUpdatingRequest {
  @NotBlank(message = "Tiêu đề lịch trình không được để trống")
  private String title;

  @NotBlank(message = "Mô tả lịch trình không được để trống")
  private String description;
}
