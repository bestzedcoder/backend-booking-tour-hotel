package com.bestzedcoder.project3.booking_tour_hotel.controller;

import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.service.ISummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("summary")
@RequiredArgsConstructor
public class DashboardController {
  private final ISummaryService summaryService;
  @GetMapping("/admin")
  public ResponseEntity<ApiResponse<?>> getSummaryAdmin() {
    ApiResponse<?> response = this.summaryService.getSummaryByAdmin();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/business")
  public ResponseEntity<ApiResponse<?>> getSummaryBusiness() {
    ApiResponse<?> response = this.summaryService.getSummaryByOwner();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/admin/revenue-by-month")
  public ResponseEntity<ApiResponse<?>> getSummaryAdminRevenue() {
    ApiResponse<?> response = this.summaryService.getRevenueByAdmin();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/admin/count-status-booking")
  public ResponseEntity<ApiResponse<?>> getSummaryAdminCountStatus() {
    ApiResponse<?> response = this.summaryService.getCountStatusByAdmin();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/admin/users-by-month")
  public ResponseEntity<ApiResponse<?>> getSummaryAdminUsers() {
    ApiResponse<?> response = this.summaryService.getUserRevenueByAdmin();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/business/revenue-by-month")
  public ResponseEntity<ApiResponse<?>> getSummaryBusinessRevenue() {
    ApiResponse<?> response = this.summaryService.getRevenueByBusiness();
    return ResponseEntity.ok(response);
  }
}
