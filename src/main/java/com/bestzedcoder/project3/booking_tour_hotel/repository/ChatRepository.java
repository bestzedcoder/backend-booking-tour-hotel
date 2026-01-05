package com.bestzedcoder.project3.booking_tour_hotel.repository;

import com.bestzedcoder.project3.booking_tour_hotel.model.Chat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRepository extends JpaRepository<Chat , Long> {
  @Query("SELECT c FROM Chat c WHERE " +
      "(c.senderId = :senderId AND c.receiverId = :receiverId) OR " +
      "(c.senderId = :receiverId AND c.receiverId = :senderId) " +
      "ORDER BY c.createdAt ASC")
  List<Chat> findFullConversation(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);

  @Query("SELECT c FROM Chat c WHERE c.id IN (" +
      "  SELECT MAX(c2.id) FROM Chat c2 " +
      "  WHERE c2.senderId = :userId OR c2.receiverId = :userId " +
      "  GROUP BY " +
      "    (CASE WHEN c2.senderId < c2.receiverId THEN c2.senderId ELSE c2.receiverId END), " +
      "    (CASE WHEN c2.senderId < c2.receiverId THEN c2.receiverId ELSE c2.senderId END)" +
      ") ORDER BY c.createdAt DESC")
  List<Chat> findLastMessagesByUser(@Param("userId") Long userId);
}
