package com.bestzedcoder.project3.booking_tour_hotel.repository;

import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingStatus;
import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingType;
import com.bestzedcoder.project3.booking_tour_hotel.model.Booking;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long> ,
    JpaSpecificationExecutor<Booking> {
  List<Booking> findByStatusAndCreatedAtBefore(BookingStatus status, LocalDateTime createdAt);
  List<Booking> findByOwner(Long owner);
  List<Booking> findByUserId(Long userId);
}
