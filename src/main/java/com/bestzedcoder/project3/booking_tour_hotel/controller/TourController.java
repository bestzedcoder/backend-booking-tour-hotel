package com.bestzedcoder.project3.booking_tour_hotel.controller;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.TourCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.TourScheduleUpdatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.TourSearchByAdminParams;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.TourSearchParams;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.TourUpdatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.PageResponse;
import com.bestzedcoder.project3.booking_tour_hotel.enums.HotelStar;
import com.bestzedcoder.project3.booking_tour_hotel.service.ITourService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("tours")
@RequiredArgsConstructor
@Tag(name = "Tour API" , description = "Quản lý tour du lịch")
public class TourController {
  private final ITourService tourService;

  @PostMapping(value = "create" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasRole('BUSINESS')")
  public ResponseEntity<ApiResponse<?>> createTour(@RequestPart("data") @Valid TourCreatingRequest tourCreatingRequest,
                                                   @RequestPart("images") MultipartFile[] images) {
    ApiResponse<?> response = this.tourService.createTour(tourCreatingRequest,images);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PutMapping(value = "/{tourId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasRole('BUSINESS')")
  public ResponseEntity<ApiResponse<?>> updateTour(@PathVariable("tourId") Long id,
                                                   @RequestPart("data") @Valid TourUpdatingRequest tourUpdatingRequest,
                                                   @RequestPart(value = "images" , required = false) MultipartFile[] imageNews) {
    ApiResponse<?> response = this.tourService.updateTour(id,tourUpdatingRequest,imageNews);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }


  @PutMapping("/{tourId}/schedule/{scheduleId}")
  @PreAuthorize("hasRole('BUSINESS')")
  public ResponseEntity<ApiResponse<?>> updateTourSchedule(@PathVariable("tourId") Long tourId,
                                                           @PathVariable("scheduleId") Long scheduleId,
                                                           @RequestBody @Valid TourScheduleUpdatingRequest tourScheduleUpdatingRequest) {
    ApiResponse<?> response = this.tourService.updateTourSchedule(tourId,scheduleId,tourScheduleUpdatingRequest);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }


  @GetMapping
  public ResponseEntity<PageResponse<?>> searchTours(TourSearchParams tourSearchParams) {
    PageResponse<?> response = this.tourService.searchByUser(tourSearchParams);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<PageResponse<?>> searchToursByAdmin(TourSearchByAdminParams tourSearchByAdminParams) {
    PageResponse<?> response = this.tourService.searchByAdmin(tourSearchByAdminParams);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/owner")
  @PreAuthorize("hasRole('BUSINESS')")
  public ResponseEntity<PageResponse<?>> searchToursByOwner(TourSearchParams tourSearchParams) {
    PageResponse<?> response = this.tourService.searchByOwner(tourSearchParams);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/{tourId}/info")
  public ResponseEntity<ApiResponse<?>> infoTourDetails(@PathVariable("tourId") Long tourId) {
    ApiResponse<?> response = this.tourService.infoTourDetails(tourId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/{tourId}/details")
  public ResponseEntity<ApiResponse<?>> searchTourDetails(@PathVariable("tourId") Long id) {
    ApiResponse<?> response = this.tourService.searchTourDetails(id);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @DeleteMapping("/{tourId}")
  public ResponseEntity<ApiResponse<?>> deleteTour(@PathVariable("tourId") Long id) {
    ApiResponse<?> response = this.tourService.deleteTour(id);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
