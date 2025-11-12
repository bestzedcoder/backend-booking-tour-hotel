package com.bestzedcoder.project3.booking_tour_hotel.dto.requests;

import com.bestzedcoder.project3.booking_tour_hotel.enums.HotelStar;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class HotelUpdatingRequest {
  @NotBlank(message = "Tên khách sạn không được để trống")
  private String hotelName;
  @NotBlank(message = "Địa chỉ không được để trống")
  private String hotelAddress;
  @NotBlank(message = "Thành phố không được để trống")
  private String hotelCity;
  @NotNull(message = "Hạng sao không được để trống")
  private HotelStar hotelStar;
  @Size(max = 1000 , message = "Mô tả tối đa 1000 ký tự")
  @NotNull(message = "Description không được để trống")
  private String hotelDescription;
  private String[] imagesOld;
}
