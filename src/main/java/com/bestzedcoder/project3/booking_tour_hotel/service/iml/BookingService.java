package com.bestzedcoder.project3.booking_tour_hotel.service.iml;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.BookingHotelRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.BookingTourRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingStatus;
import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingType;
import com.bestzedcoder.project3.booking_tour_hotel.enums.RoomStatus;
import com.bestzedcoder.project3.booking_tour_hotel.exception.ResourceNotFoundException;
import com.bestzedcoder.project3.booking_tour_hotel.model.Booking;
import com.bestzedcoder.project3.booking_tour_hotel.model.Hotel;
import com.bestzedcoder.project3.booking_tour_hotel.model.HotelBooking;
import com.bestzedcoder.project3.booking_tour_hotel.model.Room;
import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import com.bestzedcoder.project3.booking_tour_hotel.repository.BookingRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.HotelBookingRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.HotelRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.RoomRepository;
import com.bestzedcoder.project3.booking_tour_hotel.service.IBookingService;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService {
  private final HotelRepository hotelRepository;
  private final RoomRepository roomRepository;
  private final BookingRepository bookingRepository;
  private final HotelBookingRepository hotelBookingRepository;

  @Override
  @Transactional
  public ApiResponse<?> bookingHotel(BookingHotelRequest request, Long hotelId, Long roomId) {
    Hotel hotel = this.hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("hotel not found"));
    Room room = this.roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("room not found"));
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Booking booking = new Booking();
    booking.setBookingType(BookingType.HOTEL);
    booking.setStatus(BookingStatus.PENDING);
    booking.setBooking_code(this.genBookingCode());
    booking.setTotalPrice(request.getTotalPrice());
    booking.setPaymentMethod(request.getPaymentMethod());
    booking.setUser(user);
    booking = this.bookingRepository.save(booking);
    HotelBooking hotelBooking = new HotelBooking();
    hotelBooking.setBookingRoomType(request.getBookingType());
    hotelBooking.setHotelName(hotel.getHotel_name());
    hotelBooking.setHotelStar(hotel.getHotel_star());
    hotelBooking.setAddress(hotel.getHotel_address());
    hotelBooking.setCheckIn(request.getCheckIn());
    hotelBooking.setCheckOut(request.getCheckOut());
    hotelBooking.setDuration(request.getDuration());
    hotelBooking.setRoomType(room.getType());
    hotelBooking.setRoomName(room.getRoomName());
    hotelBooking.setBooking(booking);
    hotelBooking.setRoom(room);
    this.hotelBookingRepository.save(hotelBooking);
    room.setStatus(RoomStatus.BOOKED);
    this.roomRepository.save(room);
    return ApiResponse.builder().success(true).message("Booked successfully").build();
  }

  private String genBookingCode() {
    int length = 8;
    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    StringBuilder sb = new StringBuilder(length);
    SecureRandom random = new SecureRandom();
    for (int i = 0; i < length; i++) {
      int index = random.nextInt(characters.length());
      sb.append(characters.charAt(index));
    }
    return sb.toString();
  }

  @Scheduled(cron = "0 * * * * *") // chạy mỗi 1 giờ
  public void autoFailExpiredBookings() {
    System.out.println("Auto fail expired bookings");

    LocalDateTime expiredDateTime = LocalDateTime.now().minusDays(2);


    List<Booking> bookings = this.bookingRepository
        .findByStatusAndCreatedAtBefore(BookingStatus.PENDING, expiredDateTime);

    for (Booking booking : bookings) {
      booking.setStatus(BookingStatus.CANCELLED);
      HotelBooking hotelBooking = this.hotelBookingRepository.findByBookingId(booking.getId()).orElseThrow(() -> new ResourceNotFoundException("hotelBooking not found"));
      Room room = this.roomRepository.findById(hotelBooking.getRoom().getId()).orElseThrow(() -> new ResourceNotFoundException("room not found"));
      room.setStatus(RoomStatus.AVAILABLE);
      this.roomRepository.save(room);
    }

    bookingRepository.saveAll(bookings);
  }


  @Override
  public ApiResponse<?> bookingTour(BookingTourRequest request, Long tourId) {
    return null;
  }
}
