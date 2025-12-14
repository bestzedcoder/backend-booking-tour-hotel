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
import java.time.LocalDate;
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
  public ApiResponse<?> updateStatus(Long id, BookingStatus status) {
    Booking booking = this.bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("booking not found"));
    if (booking.getStatus().equals(BookingStatus.CANCELLED))
      throw new BadRequestException("Booking cancelled");
    booking.setStatus(status);
    if(status.equals(BookingStatus.CANCELLED)) {
      handleCancelBooking(booking);
    }
    this.bookingRepository.save(booking);
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

  @Override
  public PageResponse<?> getByBusiness(int page, int limit, BookingType type, BookingStatus status,
      String code, String username, LocalDate startDate, LocalDate endDate) {
    User owner = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String keyCache = String.format("search:booking:owner:%d:page:%d:limit:%d:type:%s:status:%s:code:%s:customer:%s:start:%s:end:%s",
        owner.getId(),
        page,
        limit,
        type != null ? type : "",
        status != null ? status : "",
        code != null ? code : "",
        username != null ? username : "",
        startDate != null ? startDate : "",
        endDate != null ? endDate : ""
        );
    PageResponse<BookingSearchResponse> dataCache = this.redisService.getValue(keyCache, new TypeReference<PageResponse<BookingSearchResponse>>() {});
    if (dataCache != null) {
      return dataCache;
    }

    LocalDateTime startDT = (startDate != null) ?
        startDate.atStartOfDay()
        : LocalDateTime.of(1970, 1, 1, 0, 0);

    LocalDateTime endDT = (endDate != null) ?
        endDate.plusDays(1).atStartOfDay()
        : LocalDateTime.of(9999, 12, 31, 23, 59);

    PageRequest pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());

    Specification<Booking> spec = Specification.where(
        (root, query, cb) -> cb.equal(root.get("owner"), owner.getId())
    );

    if (status != null) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
    }

    if (type != null) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("bookingType"), type));
    }


    if (code != null && !code.isEmpty()) {
      spec = spec.and((root, query, cb) ->
          cb.like(cb.lower(root.get("bookingCode")), "%" + code.toLowerCase() + "%"));
    }

    if (username != null && !username.isEmpty()) {
      spec = spec.and((root, query, cb) ->
          cb.like(cb.lower(root.get("user").get("username")), "%" + username.toLowerCase() + "%"));
    }

    // ⭐ Điều kiện createdAt FROM–TO
    spec = spec.and((root, query, cb) ->
        cb.between(root.get("createdAt"), startDT, endDT)
    );

    Page<Booking> pageResult = bookingRepository.findAll(spec, pageable);

    PageResponse<BookingSearchResponse> response = PageResponse.<BookingSearchResponse>builder()
        .success(true)
        .message("Search successful")
        .currentPages(page)
        .pageSizes(limit)
        .totalElements(pageResult.getTotalElements())
        .totalPages(pageResult.getTotalPages())
        .result(pageResult.getContent().stream().map(BookingMapper::bookingToBookingSearchResponse).toList())
        .build();

    redisService.saveKeyAndValue(keyCache, response, "1", TimeUnit.MINUTES);

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
