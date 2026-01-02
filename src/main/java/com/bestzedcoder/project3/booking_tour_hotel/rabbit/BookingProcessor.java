package com.bestzedcoder.project3.booking_tour_hotel.rabbit;

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
import com.bestzedcoder.project3.booking_tour_hotel.repository.BookingRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.HotelBookingRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.HotelRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.RoomRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.TourBookingRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.TourRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
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

  @Transactional
  public void processHotel(BookingMessage msg) {
    Room room = this.roomRepository.findByIdForUpdate(msg.getRoomId())
        .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    Hotel hotel = this.hotelRepository.findById(msg.getHotelId()).orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
    if (!room.getStatus().equals(RoomStatus.AVAILABLE)) {
      throw new BadRequestException("Phòng hiện tại đã có người đặt hoặc đang bảo trì.");
    }

    room.setStatus(RoomStatus.BOOKED);
    Booking booking = this.bookingRepository.findByBookingCode(msg.getBookingCode()).orElseThrow(() -> new ResourceNotFoundException("Booking record not found"));
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

    bookingNotifier.notifyClient(booking.getBookingCode(), "hotel", "CONFIRMED", null);
  }

  @Transactional
  public void processTour(BookingMessage msg) {

    Tour tour = this.tourRepository.findById(msg.getTourId()).orElseThrow(() -> new ResourceNotFoundException("tour not found"));
//    if (tour.getStartDate().isAfter(LocalDate.now())) {
//      throw new BadRequestException("Tour du lịch đã hết hạn đăng ký.");
//    }

    if (msg.getTourRequest().getPeople() > tour.getMaxPeople()) {
      throw new BadRequestException("Số lượng thành viên vượt quá.");
    }
    tour.setMaxPeople(tour.getMaxPeople() - msg.getTourRequest().getPeople());
    Booking booking = this.bookingRepository.findByBookingCode(msg.getBookingCode())
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

    bookingNotifier.notifyClient(booking.getBookingCode(), "tour" ,"CONFIRMED", null);
  }

  @Transactional
  public void handleBookingFailed(String bookingCode, String reason) {
    bookingRepository.findByBookingCode(bookingCode).ifPresent(booking -> {
      this.bookingRepository.delete(booking);
      String type;
      if (booking.getBookingType().equals(BookingType.HOTEL)) {
        type = "hotel";
      } else {
        type = "tour";
      }
      bookingNotifier.notifyClient(bookingCode, type ,"FAILED", reason);
    });
  }
}
