package com.bestzedcoder.project3.booking_tour_hotel.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TourScheduleCreatingRequest {
  @Min(value = 1, message = "Ngày trong lịch trình phải lớn hơn hoặc bằng 1")
  private int day;

  @NotBlank(message = "Tiêu đề lịch trình không được để trống")
  private String title;

  @NotBlank(message = "Mô tả lịch trình không được để trống")
  private String description;
}