package com.bestzedcoder.project3.booking_tour_hotel.repository;

import com.bestzedcoder.project3.booking_tour_hotel.model.TourBooking;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TourBookingRepository extends JpaRepository<TourBooking, Long> {
  Optional<TourBooking> findByBookingId(Long booingId);
}
