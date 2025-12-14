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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingProcessor {
  private final RoomRepository roomRepository;
  private final BookingRepository bookingRepository;
  private final HotelRepository hotelRepository;
  private final TourRepository tourRepository;
  private final HotelBookingRepository hotelBookingRepository;
  private final TourBookingRepository tourBookingRepository;
  private final UserRepository userRepository;

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

    Booking booking = new Booking();
    booking.setBookingType(BookingType.HOTEL);
    booking.setStatus(BookingStatus.PENDING);
    booking.setBookingCode(this.genBookingCode());
    booking.setTotalPrice(msg.getHotelRequest().getTotalPrice());
    booking.setPaymentMethod(msg.getHotelRequest().getPaymentMethod());
    booking.setOwner(hotel.getOwner().getId());
    booking.setUser(user);
    booking = this.bookingRepository.save(booking);
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
  }

  @Transactional
  public void processTour(BookingMessage msg) {

    Tour tour = this.tourRepository.findById(msg.getTourId()).orElseThrow(() -> new ResourceNotFoundException("tour not found"));
    if (msg.getTourRequest().getPeople() > tour.getMaxPeople()) {
      throw new BadRequestException("People limit exceeded");
    }
    tour.setMaxPeople(tour.getMaxPeople() - msg.getTourRequest().getPeople());
    User user = this.userRepository.findById(msg.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Booking booking = new Booking();
    booking.setBookingType(BookingType.TOUR);
    booking.setStatus(BookingStatus.PENDING);
    booking.setBookingCode(this.genBookingCode());
    booking.setPaymentMethod(msg.getTourRequest().getPaymentMethod());
    booking.setTotalPrice(msg.getTourRequest().getPeople() * tour.getPrice());
    booking.setOwner(tour.getOwner().getId());
    booking.setUser(user);
    booking = this.bookingRepository.save(booking);
    TourBooking tourBooking = new TourBooking();
    tourBooking.setBooking(booking);
    tourBooking.setTour(tour);
    tourBooking.setTourName(tour.getName());
    tourBooking.setDuration(tour.getDuration());
    tourBooking.setPeople(msg.getTourRequest().getPeople());
    tourBooking.setStartDate(tour.getStartDate());
    tourBooking.setEndDate(tour.getEndDate());
    this.tourBookingRepository.save(tourBooking);
  }
}
