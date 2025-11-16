package com.bestzedcoder.project3.booking_tour_hotel.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class TourUpdatingRequest {
  @NotBlank(message = "Tên tour không được để trống")
  private String tourName;

  @NotBlank(message = "Thành phố không được để trống")
  private String tourCity;

  @NotBlank(message = "Mô tả tour không được để trống")
  private String tourDescription;

  @NotNull(message = "Giá tour không được để trống")
  private Double tourPrice;

  @Min(value = 1, message = "Số lượng người tối đa phải lớn hơn hoặc bằng 1")
  private int maxPeople;

  private String[] imageOlds;
}
