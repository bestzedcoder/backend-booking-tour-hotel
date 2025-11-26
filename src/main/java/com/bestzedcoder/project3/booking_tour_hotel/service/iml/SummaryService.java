package com.bestzedcoder.project3.booking_tour_hotel.service.iml;

import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.SummaryBookingResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.SummaryResponse;
import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingStatus;
import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import com.bestzedcoder.project3.booking_tour_hotel.repository.BookingRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.HotelRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.TourRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.UserRepository;
import com.bestzedcoder.project3.booking_tour_hotel.service.ISummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SummaryService implements ISummaryService {
  private final UserRepository userRepository;
  private final BookingRepository bookingRepository;
  private final HotelRepository hotelRepository;
  private final TourRepository tourRepository;

  @Override
  public ApiResponse<?> getSummaryByAdmin() {
    long count_user = this.userRepository.count();
    long count_hotel = this.hotelRepository.count();
    long count_tour = this.tourRepository.count();
    long count_booking = this.bookingRepository.count();
    return ApiResponse.builder()
        .success(true)
        .data(SummaryResponse.builder()
            .totalUsers(count_user)
            .totalHotels(count_hotel)
            .totalBookings(count_booking)
            .totalTours(count_tour)
            .build())
        .build();
  }

  @Override
  public ApiResponse<?> getSummaryByOwner() {
    User owner = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    long count_hotel = this.hotelRepository.countHotelsByOwnerId(owner.getId());
    long count_tour = this.tourRepository.countToursByOwnerId(owner.getId());
    long count_booking = this.bookingRepository.countBookingsByOwner(owner.getId());
    return ApiResponse.builder()
        .success(true)
        .data(SummaryResponse.builder()
            .totalHotels(count_hotel)
            .totalTours(count_tour)
            .totalBookings(count_booking)
            .build())
        .build();
  }

  @Override
  public ApiResponse<?> getRevenueByAdmin() {
    return ApiResponse.builder()
        .success(true)
        .data(this.bookingRepository.getRevenueByMonth())
        .build();
  }

  @Override
  public ApiResponse<?> getCountStatusByAdmin() {
    long count_booking_pending = this.bookingRepository.countBookingsByStatus(BookingStatus.PENDING);
    long count_booking_confirmed = this.bookingRepository.countBookingsByStatus(BookingStatus.CONFIRMED);
    long count_booking_cancelled = this.bookingRepository.countBookingsByStatus(BookingStatus.CANCELLED);
    return ApiResponse.builder()
        .success(true)
        .data(SummaryBookingResponse.builder()
            .pending(count_booking_pending)
            .confirmed(count_booking_confirmed)
            .cancelled(count_booking_cancelled)
            .build())
        .build();
  }

  @Override
  public ApiResponse<?> getUserRevenueByAdmin() {
    return ApiResponse.builder()
        .success(true)
        .data(this.userRepository.countUsersByMonth())
        .build();
  }

  @Override
  public ApiResponse<?> getRevenueByBusiness() {
    User owner = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return ApiResponse.builder()
        .success(true)
        .data(this.bookingRepository.getRevenueByOwner(owner.getId()))
        .build();
  }
}
