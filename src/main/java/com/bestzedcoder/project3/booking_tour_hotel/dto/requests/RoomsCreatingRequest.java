package com.bestzedcoder.project3.booking_tour_hotel.dto.requests;

import com.bestzedcoder.project3.booking_tour_hotel.enums.RoomType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RoomsCreatingRequest {
  @NotNull(message = "Loại phòng (roomType) không được để trống")
  private RoomType roomType;

  @NotNull(message = "Giá theo giờ (pricePerHour) không được để trống")
  @DecimalMin(value = "0.0", inclusive = false, message = "Giá theo giờ phải lớn hơn 0")
  private Double pricePerHour;

  @NotNull(message = "Giá theo ngày (pricePerDay) không được để trống")
  @DecimalMin(value = "0.0", inclusive = false, message = "Giá theo ngày phải lớn hơn 0")
  private Double pricePerDay;

  @Min(value = 1, message = "Số lượng phòng phải ít nhất là 1")
  private int quantity;
}
