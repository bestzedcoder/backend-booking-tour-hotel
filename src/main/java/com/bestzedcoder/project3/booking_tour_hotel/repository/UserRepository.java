package com.bestzedcoder.project3.booking_tour_hotel.repository;

import com.bestzedcoder.project3.booking_tour_hotel.dto.response.MonthRevenueResponse;
import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
  User findByEmail(String email);
  User findByUsername(String username);

  boolean existsByEmail(String email);
  boolean existsByUsername(String username);

  @Query("""
        SELECT new com.bestzedcoder.project3.booking_tour_hotel.dto.response.MonthRevenueResponse(
            cast(FUNCTION('DATE_TRUNC', 'month', u.createdAt) as java.sql.Timestamp),
            COUNT(u)
        )
        FROM User u
        WHERE u.enabled = TRUE
        GROUP BY FUNCTION('DATE_TRUNC', 'month', u.createdAt)
        ORDER BY FUNCTION('DATE_TRUNC', 'month', u.createdAt)
    """)
  List<MonthRevenueResponse<Integer>> countUsersByMonth();
}
