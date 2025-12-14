package com.bestzedcoder.project3.booking_tour_hotel.repository;

import com.bestzedcoder.project3.booking_tour_hotel.model.Tour;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface TourRepository extends JpaRepository<Tour, Long>, JpaSpecificationExecutor<Tour> {
  Long countToursByOwnerId(Long tourId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT t FROM Tour t WHERE t.id = :id")
  Optional<Tour> findByIdForUpdate(Long id);

}
