package com.bestzedcoder.project3.booking_tour_hotel.rabbit;

import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingStatus;
import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingType;
import com.bestzedcoder.project3.booking_tour_hotel.enums.RoomStatus;
import com.bestzedcoder.project3.booking_tour_hotel.exception.BadRequestException;
import com.bestzedcoder.project3.booking_tour_hotel.exception.ResourceNotFoundException;
import com.bestzedcoder.project3.booking_tour_hotel.model.Booking;
import com.bestzedcoder.project3.booking_tour_hotel.model.Hotel;
import com.bestzedcoder.project3.booking_tour_hotel.model.HotelBooking;
import com.bestzedcoder.project3.booking_tour_hotel.model.Room;
import com.bestzedcoder.project3.booking_tour_hotel.model.Tour;
import com.bestzedcoder.project3.booking_tour_hotel.model.TourBooking;
import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import com.bestzedcoder.project3.booking_tour_hotel.repository.BookingRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.HotelBookingRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.HotelRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.RoomRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.TourBookingRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.TourRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingProcessor {
  private final BookingNotifier bookingNotifier;
  private final RoomRepository roomRepository;
  private final BookingRepository bookingRepository;
  private final HotelRepository hotelRepository;
  private final TourRepository tourRepository;
  private final HotelBookingRepository hotelBookingRepository;
  private final TourBookingRepository tourBookingRepository;
  private final UserRepository userRepository;

  @Transactional
  public void processHotel(BookingMessage msg) {
    Room room = this.roomRepository.findByIdForUpdate(msg.getRoomId())
        .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    Hotel hotel = this.hotelRepository.findById(msg.getHotelId()).orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
    if (room.getStatus() == RoomStatus.BOOKED) {
      throw new BadRequestException("Room already booked");
    }
    room.setStatus(RoomStatus.BOOKED);
    User user = this.userRepository.findById(msg.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Booking booking = this.bookingRepository.findById(msg.getBookingId()).orElseThrow(() -> new ResourceNotFoundException("Booking record not found"));
    booking.setTotalPrice(msg.getHotelRequest().getTotalPrice());
    booking.setOwner(hotel.getOwner().getId());
    HotelBooking hotelBooking = new HotelBooking();
    hotelBooking.setBookingRoomType(msg.getHotelRequest().getBookingType());
    hotelBooking.setHotelName(hotel.getHotelName());
    hotelBooking.setHotelStar(hotel.getHotelStar());
    hotelBooking.setAddress(hotel.getHotelAddress());
    hotelBooking.setCheckIn(msg.getHotelRequest().getCheckIn());
    hotelBooking.setCheckOut(msg.getHotelRequest().getCheckOut());
    hotelBooking.setDuration(msg.getHotelRequest().getDuration());
    hotelBooking.setRoomType(room.getType());
    hotelBooking.setRoomName(room.getRoomName());
    hotelBooking.setBooking(booking);
    hotelBooking.setRoom(room);
    this.hotelBookingRepository.save(hotelBooking);

    bookingNotifier.notifyClient(booking.getId(), "CONFIRMED", null);
  }

  @Transactional
  public void processTour(BookingMessage msg) {

    Tour tour = this.tourRepository.findById(msg.getTourId()).orElseThrow(() -> new ResourceNotFoundException("tour not found"));
    if (msg.getTourRequest().getPeople() > tour.getMaxPeople()) {
      throw new BadRequestException("Số lượng thành viên vượt quá.");
    }
    tour.setMaxPeople(tour.getMaxPeople() - msg.getTourRequest().getPeople());
    User user = this.userRepository.findById(msg.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Booking booking = this.bookingRepository.findById(msg.getBookingId())
        .orElseThrow(() -> new ResourceNotFoundException("Booking record not found"));

    booking.setTotalPrice(msg.getTourRequest().getPeople() * tour.getPrice());
    booking.setOwner(tour.getOwner().getId());
    TourBooking tourBooking = new TourBooking();
    tourBooking.setBooking(booking);
    tourBooking.setTour(tour);
    tourBooking.setTourName(tour.getName());
    tourBooking.setDuration(tour.getDuration());
    tourBooking.setPeople(msg.getTourRequest().getPeople());
    tourBooking.setStartDate(tour.getStartDate());
    tourBooking.setEndDate(tour.getEndDate());
    this.tourBookingRepository.save(tourBooking);

    bookingNotifier.notifyClient(booking.getId(), "CONFIRMED", null);
  }

  @Transactional
  public void updateBookingStatusFailed(Long bookingId, String reason) {
    bookingRepository.findById(bookingId).ifPresent(booking -> {
      // Cần thêm trường failureReason vào Booking Entity
      // booking.setFailureReason(reason);
      booking.setStatus(BookingStatus.CANCELLED);
      bookingRepository.save(booking);

      // GỌI NOTIFIER: Đẩy thông báo thất bại
      bookingNotifier.notifyClient(bookingId, "FAILED", reason);
    });
  }
}
