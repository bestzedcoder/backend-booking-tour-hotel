package com.bestzedcoder.project3.booking_tour_hotel.repository;

import com.bestzedcoder.project3.booking_tour_hotel.dto.response.MonthRevenueResponse;
import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingStatus;
import com.bestzedcoder.project3.booking_tour_hotel.model.Booking;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<Booking, Long> ,
    JpaSpecificationExecutor<Booking> {
  List<Booking> findByStatusAndCreatedAtBefore(BookingStatus status, LocalDateTime createdAt);
  List<Booking> findByOwner(Long owner);
  List<Booking> findByUserId(Long userId);
  Long countBookingsByOwner(Long owner);

  @Query("""
    SELECT new com.bestzedcoder.project3.booking_tour_hotel.dto.response.MonthRevenueResponse(
             cast(FUNCTION('DATE_TRUNC', 'month', b.createdAt) as java.sql.Timestamp),
             SUM(b.totalPrice)
         )
    FROM Booking b
    WHERE b.status = com.bestzedcoder.project3.booking_tour_hotel.enums.BookingStatus.CONFIRMED
    GROUP BY FUNCTION('DATE_TRUNC', 'month', b.createdAt)
    ORDER BY FUNCTION('DATE_TRUNC', 'month', b.createdAt)
""")
  List<MonthRevenueResponse<Double>> getRevenueByMonth();


  @Query("""
    SELECT new com.bestzedcoder.project3.booking_tour_hotel.dto.response.MonthRevenueResponse(
             cast(FUNCTION('DATE_TRUNC', 'month', b.createdAt) as java.sql.Timestamp),
             SUM(b.totalPrice)
         )
    FROM Booking b
    WHERE b.status = com.bestzedcoder.project3.booking_tour_hotel.enums.BookingStatus.CONFIRMED
    AND b.owner = :ownerId
    GROUP BY FUNCTION('DATE_TRUNC', 'month', b.createdAt)
    ORDER BY FUNCTION('DATE_TRUNC', 'month', b.createdAt)
""")
  List<MonthRevenueResponse<Double>> getRevenueByOwner(Long ownerId);


  @Query("""
    SELECT count(b)
    FROM Booking b
    WHERE b.status = :status
""")
  long countBookingsByStatus(BookingStatus status);
}
