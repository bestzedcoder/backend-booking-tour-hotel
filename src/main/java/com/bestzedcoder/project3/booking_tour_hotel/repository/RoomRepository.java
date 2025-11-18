package com.bestzedcoder.project3.booking_tour_hotel.repository;

import com.bestzedcoder.project3.booking_tour_hotel.enums.RoomStatus;
import com.bestzedcoder.project3.booking_tour_hotel.model.Room;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepository extends JpaRepository<Room , Long> {
  @Modifying
  @Transactional
  @Query("UPDATE Room r SET r.pricePerDay = :pricePerDay, r.pricePerHour = :pricePerHour, r.status = :status WHERE r.id = :roomId AND r.hotel.id = :hotelId")
  int updateRoomByHotelId(
      @Param("hotelId") Long hotelId,
      @Param("roomId") Long roomId,
      @Param("pricePerDay") Double pricePerDay,
      @Param("pricePerHour") Double pricePerHour,
      @Param("status") RoomStatus status
  );

  Room findByRoomName(String roomName);
}
