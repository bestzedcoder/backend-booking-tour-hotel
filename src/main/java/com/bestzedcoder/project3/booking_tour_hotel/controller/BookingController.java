package com.bestzedcoder.project3.booking_tour_hotel.controller;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.BookingHotelRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.BookingTourRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.BookingUpdatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.PageResponse;
import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingStatus;
import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingType;
import com.bestzedcoder.project3.booking_tour_hotel.enums.PaymentMethod;
import com.bestzedcoder.project3.booking_tour_hotel.mapper.BookingMapper;
import com.bestzedcoder.project3.booking_tour_hotel.model.Booking;
import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import com.bestzedcoder.project3.booking_tour_hotel.rabbit.BookingMessage;
import com.bestzedcoder.project3.booking_tour_hotel.rabbit.BookingProducer;
import com.bestzedcoder.project3.booking_tour_hotel.service.IBookingService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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

@RestController
@RequestMapping("bookings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class BookingController {
  private final IBookingService bookingService;
  private final BookingProducer bookingProducer;
  private final BookingMapper bookingMapper;

  @PostMapping("/hotel/{hotelId}/room/{roomId}")
  public ResponseEntity<ApiResponse<?>> bookingHotel(@RequestBody BookingHotelRequest bookingHotelRequest, @PathVariable Long hotelId, @PathVariable Long roomId) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Booking booking = this.bookingMapper.createBooking(bookingHotelRequest.getPaymentMethod(), BookingType.HOTEL, user);
    BookingMessage msg = new BookingMessage();
    msg.setUserId(user.getId());
    msg.setBookingId(booking.getId());
    msg.setBookingType(BookingType.HOTEL);
    msg.setHotelId(hotelId);
    msg.setRoomId(roomId);
    msg.setHotelRequest(bookingHotelRequest);

    bookingProducer.send(msg);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.builder()
            .success(true)
            .message("Booking is being processed")
            .data(booking.getId())
            .build());
  }

  @PostMapping("/tour/{tourId}")
  public ResponseEntity<ApiResponse<?>> bookingTour(@PathVariable("tourId") Long tourId, @RequestBody
      BookingTourRequest bookingTourRequest) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Booking booking = this.bookingMapper.createBooking(bookingTourRequest.getPaymentMethod(), BookingType.TOUR, user);
    BookingMessage msg = new BookingMessage();
    msg.setUserId(user.getId());
    msg.setBookingId(booking.getId());
    msg.setBookingType(BookingType.TOUR);
    msg.setTourId(tourId);
    msg.setTourRequest(bookingTourRequest);

    bookingProducer.send(msg);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.builder()
            .success(true)
            .message("Booking is being processed")
            .data(booking.getId())
            .build());
  }

  @PutMapping("/update-status/{bookingId}")
  @PreAuthorize("hasRole('BUSINESS')")
  public ResponseEntity<ApiResponse<?>> updateBookingStatus(@RequestBody BookingUpdatingRequest request, @PathVariable Long bookingId) {
    ApiResponse<?> response = this.bookingService.updateStatus(bookingId , request.getStatus());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/by-admin")
  public ResponseEntity<PageResponse<?>> getByAdminV2(
      @RequestParam(value = "page" , defaultValue = "1") int page,
      @RequestParam(value = "limit" , defaultValue = "10") int limit,
      @RequestParam(value = "status" , required = false) BookingStatus status,
      @RequestParam(value = "booking_type" , required = false) BookingType type,
      @RequestParam(value = "booking_code" ,required = false) String code,
      @RequestParam(value = "customer" , required = false) String customer,
      @RequestParam(value = "payment_method" , required = false) PaymentMethod method
  ) {
    PageResponse<?> response = this.bookingService.getByAdminV1(page , limit , status , code , customer , method , type);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/by-admin/business")
  public ResponseEntity<ApiResponse<?>> getByAdminV1(@RequestParam("business") String name) {
    ApiResponse<?> response = this.bookingService.getByAdminV2(name);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/by-business")
  public ResponseEntity<PageResponse<?>> getByBusiness(
      @RequestParam(value = "page" , defaultValue = "1") int page,
      @RequestParam(value = "limit" , defaultValue = "10") int limit,
      @RequestParam(value = "type", required = false) BookingType type,
      @RequestParam(value = "status" , required = false) BookingStatus status,
      @RequestParam(value = "customer" , required = false) String customer,
      @RequestParam(value = "code" , required = false) String code,
      @RequestParam(value = "start_date" , required = false) LocalDate startDate,
      @RequestParam(value = "end_date" , required = false) LocalDate endDate
  ) {
    PageResponse<?> response = this.bookingService.getByBusiness(page , limit , type , status , code , customer , startDate , endDate);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/by-customer")
  public ResponseEntity<ApiResponse<?>> getByCustomer() {
    ApiResponse<?> response = this.bookingService.getByCustomer();
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/invoice/{bookingId}")
  public ResponseEntity<ApiResponse<?>> getInvoiceById(@PathVariable Long bookingId) {
    ApiResponse<?> response = this.bookingService.invoice(bookingId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @DeleteMapping("/{bookingId}")
  public ResponseEntity<ApiResponse<?>> deleteBooking(@PathVariable Long bookingId) {
    ApiResponse<?> response = this.bookingService.deleteBooking(bookingId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
