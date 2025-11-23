package com.bestzedcoder.project3.booking_tour_hotel.controller;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.TourCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.TourScheduleUpdatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.TourSearchByAdminParams;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.TourSearchParams;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.TourUpdatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.PageResponse;
import com.bestzedcoder.project3.booking_tour_hotel.service.ITourService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("tours")
@RequiredArgsConstructor
@Tag(name = "Tour API", description = "Quản lý tour du lịch")
public class TourController {

  private final ITourService tourService;

  // ---------------------------------------------
  // CREATE TOUR
  // ---------------------------------------------
  @Operation(
      summary = "Tạo tour mới",
      description = "Doanh nghiệp tạo tour mới (upload nhiều ảnh)"
  )
  @PostMapping(value = "create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasRole('BUSINESS')")
  public ResponseEntity<ApiResponse<?>> createTour(
      @Parameter(
          description = "Thông tin tour",
          required = true
      )
      @RequestPart("data") @Valid TourCreatingRequest tourCreatingRequest,

      @Parameter(
          description = "Danh sách ảnh của tour",
          array = @ArraySchema(schema = @Schema(type = "string", format = "binary"))
      )
      @RequestPart("images") MultipartFile[] images
  ) {
    ApiResponse<?> response = this.tourService.createTour(tourCreatingRequest, images);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  // ---------------------------------------------
  // UPDATE TOUR
  // ---------------------------------------------
  @Operation(
      summary = "Cập nhật tour",
      description = "Doanh nghiệp cập nhật thông tin tour + upload thêm ảnh"
  )
  @PutMapping(value = "/{tourId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasRole('BUSINESS')")
  public ResponseEntity<ApiResponse<?>> updateTour(
      @Parameter(description = "ID tour", required = true)
      @PathVariable("tourId") Long id,

      @Parameter(description = "Thông tin cập nhật tour")
      @RequestPart("data") @Valid TourUpdatingRequest tourUpdatingRequest,

      @Parameter(
          description = "Ảnh mới thêm",
          array = @ArraySchema(schema = @Schema(type = "string", format = "binary"))
      )
      @RequestPart(value = "images", required = false) MultipartFile[] imageNews
  ) {
    ApiResponse<?> response = this.tourService.updateTour(id, tourUpdatingRequest, imageNews);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // ---------------------------------------------
  // UPDATE TOUR SCHEDULE
  // ---------------------------------------------
  @Operation(
      summary = "Cập nhật lịch trình của tour",
      description = "Doanh nghiệp cập nhật một schedule cụ thể"
  )
  @PutMapping("/{tourId}/schedule/{scheduleId}")
  @PreAuthorize("hasRole('BUSINESS')")
  public ResponseEntity<ApiResponse<?>> updateTourSchedule(
      @Parameter(description = "ID tour", required = true)
      @PathVariable("tourId") Long tourId,

      @Parameter(description = "ID lịch trình", required = true)
      @PathVariable("scheduleId") Long scheduleId,


      @RequestBody @Valid TourScheduleUpdatingRequest tourScheduleUpdatingRequest
  ) {
    ApiResponse<?> response = this.tourService.updateTourSchedule(tourId, scheduleId, tourScheduleUpdatingRequest);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // ---------------------------------------------
  // SEARCH BY USER
  // ---------------------------------------------
  @Operation(
      summary = "Search tour dành cho tất cả user",
      description = "Search tour theo tên, thành phố, giá, ngày..."
  )
  @GetMapping
  public ResponseEntity<PageResponse<?>> searchTours(
      @Parameter(description = "Params search tour")
      TourSearchParams tourSearchParams
  ) {
    PageResponse<?> response = this.tourService.searchByUser(tourSearchParams);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // ---------------------------------------------
  // SEARCH BY ADMIN
  // ---------------------------------------------
  @Operation(
      summary = "Admin search tất cả tour",
      description = "ADMIN xem danh sách tour (phân trang + filter)"
  )
  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<PageResponse<?>> searchToursByAdmin(
      @Parameter(description = "Params search dành cho admin")
      TourSearchByAdminParams tourSearchByAdminParams
  ) {
    PageResponse<?> response = this.tourService.searchByAdmin(tourSearchByAdminParams);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // ---------------------------------------------
  // SEARCH BY OWNER
  // ---------------------------------------------
  @Operation(
      summary = "Doanh nghiệp xem danh sách tour của mình",
      description = "Search tour theo owner đang login"
  )
  @GetMapping("/owner")
  @PreAuthorize("hasRole('BUSINESS')")
  public ResponseEntity<PageResponse<?>> searchToursByOwner(
      @Parameter(description = "Params search dành cho doanh nghiệp")
      TourSearchParams tourSearchParams
  ) {
    PageResponse<?> response = this.tourService.searchByOwner(tourSearchParams);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // ---------------------------------------------
  // TOUR INFO
  // ---------------------------------------------
  @Operation(summary = "Xem thông tin tour", description = "Thông tin tour cơ bản")
  @GetMapping("/{tourId}/info")
  public ResponseEntity<ApiResponse<?>> infoTourDetails(
      @Parameter(description = "ID tour", required = true)
      @PathVariable("tourId") Long tourId
  ) {
    ApiResponse<?> response = this.tourService.infoTourDetails(tourId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // ---------------------------------------------
  // TOUR DETAILS
  // ---------------------------------------------
  @Operation(summary = "Xem thông tin đầy đủ tour", description = "Bao gồm lịch trình, địa điểm...")
  @GetMapping("/{tourId}/details")
  public ResponseEntity<ApiResponse<?>> searchTourDetails(
      @Parameter(description = "ID tour", required = true)
      @PathVariable("tourId") Long id
  ) {
    ApiResponse<?> response = this.tourService.searchTourDetails(id);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }


  @Operation(summary = "Xem thông tin tour để booking", description = "Bao gồm lịch trình, địa điểm...")
  @GetMapping("/{tourId}/booking-info")
  public ResponseEntity<ApiResponse<?>> tourBookingInfo(@PathVariable("tourId") Long tourId) {
    ApiResponse<?> response = this.tourService.tourBookingInfo(tourId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // ---------------------------------------------
  // DELETE TOUR
  // ---------------------------------------------
  @Operation(summary = "Xóa tour", description = "Xóa tour theo ID")
  @DeleteMapping("/{tourId}")
  public ResponseEntity<ApiResponse<?>> deleteTour(
      @Parameter(description = "ID tour", required = true)
      @PathVariable("tourId") Long id
  ) {
    ApiResponse<?> response = this.tourService.deleteTour(id);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
