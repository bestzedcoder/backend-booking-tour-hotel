package com.bestzedcoder.project3.booking_tour_hotel.repository;

import com.bestzedcoder.project3.booking_tour_hotel.enums.HotelStar;
import com.bestzedcoder.project3.booking_tour_hotel.model.Hotel;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
  List<Hotel> findByOwnerId(Long ownerId);

  @Query("""
        SELECT h
        FROM Hotel h
        WHERE
            (:hotelName IS NULL OR LOWER(h.hotel_name) LIKE LOWER(CONCAT('%', :hotelName, '%')))
        AND (:address IS NULL OR LOWER(h.hotel_address) LIKE LOWER(CONCAT('%', :address, '%')))
        AND (:city IS NULL OR LOWER(h.hotel_city) LIKE LOWER(CONCAT('%', :city, '%')))
        AND (:hotelStar IS NULL OR h.hotel_star = :hotelStar)
    """)
  Page<Hotel> searchHotels(
      @Param("hotelName") String hotelName,
      @Param("address") String address,
      @Param("city") String city,
      @Param("hotelStar") HotelStar hotelStar,
      Pageable pageable
  );
}
