package com.bestzedcoder.project3.booking_tour_hotel.service;

import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;

public interface ISummaryService {
  ApiResponse<?> getSummaryByAdmin();
  ApiResponse<?> getSummaryByOwner();
  ApiResponse<?> getRevenueByAdmin();
  ApiResponse<?> getCountStatusByAdmin();
  ApiResponse<?> getUserRevenueByAdmin();
  ApiResponse<?> getRevenueByBusiness();
}
