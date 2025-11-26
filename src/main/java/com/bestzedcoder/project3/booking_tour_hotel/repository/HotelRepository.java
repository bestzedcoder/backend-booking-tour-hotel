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
  Page<Hotel> findByOwnerId(Long ownerId,Pageable pageable);

  @Query("""
        SELECT h
        FROM Hotel h
        WHERE
            (:hotelName IS NULL OR LOWER(h.hotelName) LIKE LOWER(CONCAT('%', :hotelName, '%')))
        AND (:city IS NULL OR LOWER(h.hotelCity) LIKE LOWER(CONCAT('%', :city, '%')))
        AND (:hotelStar IS NULL OR h.hotelStar = :hotelStar)
    """)
  Page<Hotel> searchHotels(
      @Param("hotelName") String hotelName,
      @Param("city") String city,
      @Param("hotelStar") HotelStar hotelStar,
      Pageable pageable
  );


  @Query("""
    SELECT h
    FROM Hotel h
    WHERE
        h.owner.id = :ownerId
        AND (:hotelName IS NULL OR LOWER(h.hotelName) LIKE LOWER(CONCAT('%', :hotelName, '%')))
        AND (:city IS NULL OR LOWER(h.hotelCity) LIKE LOWER(CONCAT('%', :city, '%')))
        AND (:hotelStar IS NULL OR h.hotelStar = :hotelStar)
""")
  Page<Hotel> searchHotelsByOwnerId(
      @Param("ownerId") Long ownerId,
      @Param("hotelName") String hotelName,
      @Param("city") String city,
      @Param("hotelStar") HotelStar hotelStar,
      Pageable pageable
  );

  Long countHotelsByOwnerId(Long ownerId);
}
