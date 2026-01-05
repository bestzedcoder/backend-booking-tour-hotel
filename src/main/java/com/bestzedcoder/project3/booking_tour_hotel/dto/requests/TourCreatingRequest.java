package com.bestzedcoder.project3.booking_tour_hotel.dto.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class TourCreatingRequest {
  @NotBlank(message = "Tên tour không được để trống")
  private String tourName;

  @NotBlank(message = "Thành phố không được để trống")
  private String tourCity;

  @NotBlank(message = "Mô tả tour không được để trống")
  private String tourDescription;

  @NotNull(message = "Giá tour không được để trống")
  private Double tourPrice;

  @NotNull(message = "Ngày bắt đầu không được để trống")
  private LocalDate startDate;

  @NotNull(message = "Ngày kết thúc không được để trống")
  private LocalDate endDate;

//  @NotNull(message = "Hạn đăng ký không được để trống")
//  private LocalDate deadline;

  @Min(value = 1, message = "Thời lượng tour phải lớn hơn hoặc bằng 1 ngày")
  private int duration;

  @Min(value = 1, message = "Số lượng người tối đa phải lớn hơn hoặc bằng 1")
  private int maxPeople;

  @Valid
  @NotEmpty(message = "Lịch trình tour không được để trống")
  private TourScheduleCreatingRequest[] tourSchedule;
}
