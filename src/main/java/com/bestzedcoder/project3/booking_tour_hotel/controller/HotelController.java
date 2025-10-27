package com.bestzedcoder.project3.booking_tour_hotel.controller;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.HotelCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.RoomsCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.PageResponse;
import com.bestzedcoder.project3.booking_tour_hotel.enums.HotelStar;
import com.bestzedcoder.project3.booking_tour_hotel.service.IHotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("hotels")
@RequiredArgsConstructor
public class HotelController {
  private final IHotelService hotelService;

  @PostMapping(value = "create" , consumes = {"multipart/form-data"})
  @PreAuthorize("hasRole('BUSINESS')")
  public ResponseEntity<ApiResponse<?>> create(@RequestPart("data") @Valid HotelCreatingRequest hotelCreatingRequest , @RequestPart(value = "images") MultipartFile[] images) {
    ApiResponse<?> response = this.hotelService.create(hotelCreatingRequest , images);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{ownerId}")
  @PreAuthorize("hasRole('BUSINESS')")
  public ResponseEntity<ApiResponse<?>> getHotelsByOwnerId(@PathVariable("ownerId") Long id) {
    ApiResponse<?> response = this.hotelService.getHotelsByOwnerId(id);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PostMapping("/{hotelId}/rooms")
  @PreAuthorize("hasRole('BUSINESS')")
  public ResponseEntity<ApiResponse<?>> createRooms(@PathVariable("hotelId") Long id,@RequestBody
      RoomsCreatingRequest roomsCreatingRequest) {
    ApiResponse<?> response = this.hotelService.createRoom(id,roomsCreatingRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/search")
  public ResponseEntity<PageResponse<?>> searchHotelsByUser(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit,
      @RequestParam(required = false) String hotelName,
      @RequestParam(required = false) String address,
      @RequestParam(required = false) String city,
      @RequestParam(required = false) HotelStar hotelStar) {
    PageResponse<?> response = this.hotelService.searchByUser(page,limit,hotelName,address,city,hotelStar);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
