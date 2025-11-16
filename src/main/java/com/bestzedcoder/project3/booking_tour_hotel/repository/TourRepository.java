package com.bestzedcoder.project3.booking_tour_hotel.repository;

import com.bestzedcoder.project3.booking_tour_hotel.model.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TourRepository extends JpaRepository<Tour, Long>, JpaSpecificationExecutor<Tour> {

}
