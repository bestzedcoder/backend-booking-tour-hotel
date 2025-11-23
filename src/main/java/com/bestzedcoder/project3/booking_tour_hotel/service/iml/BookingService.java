package com.bestzedcoder.project3.booking_tour_hotel.service.iml;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.BookingHotelRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.BookingTourRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.BookingCustomerResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.BookingSearchResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.PageResponse;
import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingStatus;
import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingType;
import com.bestzedcoder.project3.booking_tour_hotel.enums.PaymentMethod;
import com.bestzedcoder.project3.booking_tour_hotel.enums.RoomStatus;
import com.bestzedcoder.project3.booking_tour_hotel.exception.BadRequestException;
import com.bestzedcoder.project3.booking_tour_hotel.exception.ResourceNotFoundException;
import com.bestzedcoder.project3.booking_tour_hotel.mail.ContentInvoiceHotel;
import com.bestzedcoder.project3.booking_tour_hotel.mail.ContentInvoiceTour;
import com.bestzedcoder.project3.booking_tour_hotel.mail.IEmailService;
import com.bestzedcoder.project3.booking_tour_hotel.mapper.BookingMapper;
import com.bestzedcoder.project3.booking_tour_hotel.model.Booking;
import com.bestzedcoder.project3.booking_tour_hotel.model.Hotel;
import com.bestzedcoder.project3.booking_tour_hotel.model.HotelBooking;
import com.bestzedcoder.project3.booking_tour_hotel.model.Room;
import com.bestzedcoder.project3.booking_tour_hotel.model.Tour;
import com.bestzedcoder.project3.booking_tour_hotel.model.TourBooking;
import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import com.bestzedcoder.project3.booking_tour_hotel.redis.IRedisService;
import com.bestzedcoder.project3.booking_tour_hotel.repository.BookingRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.HotelBookingRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.HotelRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.RoomRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.TourBookingRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.TourRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.UserRepository;
import com.bestzedcoder.project3.booking_tour_hotel.service.IBookingService;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService {
  private final HotelRepository hotelRepository;
  private final TourRepository tourRepository;
  private final RoomRepository roomRepository;
  private final BookingRepository bookingRepository;
  private final HotelBookingRepository hotelBookingRepository;
  private final TourBookingRepository tourBookingRepository;
  private final UserRepository userRepository;
  private final IRedisService redisService;
  private final IEmailService emailService;

  @Override
  @Transactional
  public ApiResponse<?> bookingHotel(BookingHotelRequest request, Long hotelId, Long roomId) {
    Hotel hotel = this.hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("hotel not found"));
    Room room = this.roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("room not found"));
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Booking booking = new Booking();
    booking.setBookingType(BookingType.HOTEL);
    booking.setStatus(BookingStatus.PENDING);
    booking.setBookingCode(this.genBookingCode());
    booking.setTotalPrice(request.getTotalPrice());
    booking.setPaymentMethod(request.getPaymentMethod());
    booking.setOwner(hotel.getOwner().getId());
    booking.setUser(user);
    booking = this.bookingRepository.save(booking);
    HotelBooking hotelBooking = new HotelBooking();
    hotelBooking.setBookingRoomType(request.getBookingType());
    hotelBooking.setHotelName(hotel.getHotelName());
    hotelBooking.setHotelStar(hotel.getHotelStar());
    hotelBooking.setAddress(hotel.getHotelAddress());
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

    LocalDateTime expiredDateTime = LocalDateTime.now().minusMinutes(10);


    List<Booking> bookings = this.bookingRepository
        .findByStatusAndCreatedAtBefore(BookingStatus.PENDING, expiredDateTime);

    for (Booking booking : bookings) {
      booking.setStatus(BookingStatus.CANCELLED);
      handleCancelBooking(booking);
    }

    bookingRepository.saveAll(bookings);
  }

  @Override
  @Transactional
  public ApiResponse<?> bookingTour(BookingTourRequest request, Long tourId) {
    Tour tour = this.tourRepository.findById(tourId).orElseThrow(() -> new ResourceNotFoundException("tour not found"));
    if (request.getPeople() > tour.getMaxPeople()) {
      throw new BadRequestException("People limit exceeded");
    }
    tour.setMaxPeople(tour.getMaxPeople() - request.getPeople());
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Booking booking = new Booking();
    booking.setBookingType(BookingType.TOUR);
    booking.setStatus(BookingStatus.PENDING);
    booking.setBookingCode(this.genBookingCode());
    booking.setPaymentMethod(request.getPaymentMethod());
    booking.setTotalPrice(request.getPeople() * tour.getPrice());
    booking.setOwner(tour.getOwner().getId());
    booking.setUser(user);
    booking = this.bookingRepository.save(booking);
    TourBooking tourBooking = new TourBooking();
    tourBooking.setBooking(booking);
    tourBooking.setTour(tour);
    tourBooking.setTourName(tour.getName());
    tourBooking.setDuration(tour.getDuration());
    tourBooking.setPeople(request.getPeople());
    tourBooking.setStartDate(tour.getStartDate());
    tourBooking.setEndDate(tour.getEndDate());
    this.tourBookingRepository.save(tourBooking);
    return ApiResponse.builder().success(true).message("Booked successfully").build();
  }

  @Override
  public ApiResponse<?> updateStatus(Long id, BookingStatus status) {
    Booking booking = this.bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("booking not found"));
    booking.setStatus(status);
    if(status.equals(BookingStatus.CANCELLED)) {
      handleCancelBooking(booking);
    }
    return ApiResponse.builder().success(true).message("Updated status booking successfully").build();
  }

  @Override
  public ApiResponse<?> getByAdminV2(String name) {
    User user = this.userRepository.findByUsername(name);
    if (user == null) {
      throw new ResourceNotFoundException("user not found");
    }
    List<Booking> bookings = this.bookingRepository.findByOwner(user.getId());
    return ApiResponse.builder()
        .success(true)
        .message("Booking found")
        .data(bookings.stream().map(BookingMapper::bookingToBookingSearchResponse).toList())
        .build();
  }

  @Override
  public ApiResponse<?> deleteBooking(Long id) {
    Booking booking = this.bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("booking not found"));
    if (booking.getStatus().equals(BookingStatus.PENDING)) {
      throw new BadRequestException("Booking is already pending");
    }
    if (booking.getBookingType().equals(BookingType.TOUR)) {
      TourBooking tourBooking = this.tourBookingRepository.findByBookingId(id).orElseThrow(() -> new ResourceNotFoundException("tour booking not found"));
      this.tourBookingRepository.delete(tourBooking);
    } else {
      HotelBooking hotelBooking = this.hotelBookingRepository.findByBookingId(id).orElseThrow(() -> new ResourceNotFoundException("hotel booking not found"));
      this.hotelBookingRepository.delete(hotelBooking);
    }
    this.bookingRepository.delete(booking);
    return ApiResponse.builder().success(true).message("Deleted successfully").build();
  }

  @Override
  public ApiResponse<?> invoice(Long id) {
    Booking booking = this.bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("booking not found"));
    if (booking.getStatus().equals(BookingStatus.CANCELLED)) {
      throw new BadRequestException("Booking đã bị hủy");
    }

    User customer = booking.getUser();
    if (booking.getBookingType().equals(BookingType.HOTEL)) {
      HotelBooking hotelBooking = this.hotelBookingRepository.findByBookingId(id).orElseThrow(() -> new ResourceNotFoundException("hotel booking not found"));
      ContentInvoiceHotel cnt = ContentInvoiceHotel.builder()
          .to(customer.getEmail())
          .bookingCode(booking.getBookingCode())
          .hotelName(hotelBooking.getHotelName())
          .hotelAddress(hotelBooking.getAddress())
          .hotelStar(hotelBooking.getHotelStar())
          .duration(hotelBooking.getDuration())
          .roomName(hotelBooking.getRoomName())
          .roomType(hotelBooking.getRoomType())
          .checkIn(hotelBooking.getCheckIn())
          .checkOut(hotelBooking.getCheckOut())
          .paymentMethod(booking.getPaymentMethod())
          .totalPrice(booking.getTotalPrice())
          .status(booking.getStatus())
          .bookingRoomType(hotelBooking.getBookingRoomType())
          .build();
      this.emailService.sendInvoiceHotelEmail(cnt);
    } else {
      ContentInvoiceTour cnt = ContentInvoiceTour.builder().build();
      this.emailService.sendInvoiceTourEmail(cnt);
    }
    return ApiResponse.builder().success(true).message("Bill đã được gửi thành công. Vui lòng vào email để xác nhận!").build();
  }

  @Override
  public PageResponse<?> getByAdminV1(int page, int limit, BookingStatus status, String code,
      String customer , PaymentMethod method , BookingType type) {
    String keyCache = String.format(
        "search:booking:page:%d:limit:%d:status:%s:code:%s:customer:%s:method:%s:type:%s",
        page,
        limit,
        status != null ? status : "null",
        code != null ? code : "null",
        customer != null ? customer : "null",
        method != null ? method : "null",
        type != null ? type : "null"
    );

    PageResponse<BookingSearchResponse> dataCache = this.redisService.getValue(keyCache,
        new TypeReference<PageResponse<BookingSearchResponse>>() {});

    if (dataCache != null) {
      return dataCache;
    }

    PageRequest pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());

    Specification<Booking> spec = Specification.where(null);

    if (status != null) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
    }

    if (code != null && !code.isEmpty()) {
      spec = spec.and((root, query, cb) -> cb.like(root.get("bookingCode"), "%" + code + "%"));
    }

    if (customer != null && !customer.isEmpty()) {
      spec = spec.and((root, query, cb) ->
          cb.like(root.get("user").get("username"), "%" + customer + "%"));
    }

    if (method != null) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("paymentMethod"), method));
    }

    if (type != null) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("bookingType"), type));
    }

    Page<Booking> pageData = bookingRepository.findAll(spec, pageable);

    PageResponse<BookingSearchResponse> response = PageResponse.<BookingSearchResponse>builder()
        .success(true)
        .message("Search successful")
        .pageSizes(limit)
        .currentPages(page)
        .totalPages(pageData.getTotalPages())
        .totalElements(pageData.getTotalElements())
        .result(pageData.getContent().stream().map(BookingMapper::bookingToBookingSearchResponse).toList())
        .build();
    this.redisService.saveKeyAndValue(keyCache , response , "2" , TimeUnit.MINUTES);
    return response;
  }

  @Override
  public ApiResponse<?> getByCustomer() {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//    String keyCache = String.format("search:booking:customer:%d", user.getId());
//    ApiResponse<BookingCustomerResponse<?>>  dataCache = this.redisService.getValue(keyCache,
//        new TypeReference<ApiResponse<BookingCustomerResponse<?>>>() {});
//    if (dataCache != null) {
//      return dataCache;
//    }
    List<Booking> bookings = this.bookingRepository.findByUserId(user.getId());
    ApiResponse<List<BookingCustomerResponse<?>>> response =
        ApiResponse.<List<BookingCustomerResponse<?>>>builder()
            .success(true)
            .message("Search successful")
            .data(
                bookings.stream().map(booking -> {
                  if (booking.getBookingType() == BookingType.HOTEL) {
                    HotelBooking hotelBooking =
                        hotelBookingRepository
                            .findByBookingId(booking.getId())
                            .orElseThrow(RuntimeException::new);

                    return BookingMapper.toHotelBookingResponse(booking, hotelBooking);
                  } else {
                    TourBooking tourBooking =
                        tourBookingRepository
                            .findByBookingId(booking.getId())
                            .orElseThrow(RuntimeException::new);

                    return BookingMapper.toTourBookingResponse(booking, tourBooking);
                  }
                }).toList()
            )
            .build();
//    this.redisService.saveKeyAndValue(keyCache , response , "1" , TimeUnit.MINUTES);
    return response;
  }

  private void handleCancelBooking(Booking booking) {
    switch (booking.getBookingType()) {
      case HOTEL -> cancelHotelBooking(booking.getId());
      case TOUR  -> cancelTourBooking(booking.getId());
    }
  }

  private void cancelHotelBooking(Long bookingId) {
    HotelBooking hotelBooking = hotelBookingRepository.findByBookingId(bookingId)
        .orElseThrow(() -> new ResourceNotFoundException("hotelBooking not found"));

    Room room = hotelBooking.getRoom();
    room.setStatus(RoomStatus.AVAILABLE);
    roomRepository.save(room);
  }

  private void cancelTourBooking(Long bookingId) {
    TourBooking tourBooking = tourBookingRepository.findByBookingId(bookingId)
        .orElseThrow(() -> new ResourceNotFoundException("tourBooking not found"));

    Tour tour = tourBooking.getTour();
    tour.setMaxPeople(tour.getMaxPeople() + tourBooking.getPeople());
    tourRepository.save(tour);
  }
}
