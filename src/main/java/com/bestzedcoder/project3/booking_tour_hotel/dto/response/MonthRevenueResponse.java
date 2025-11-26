package com.bestzedcoder.project3.booking_tour_hotel.dto.response;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthRevenueResponse <T> {
  private Timestamp month;
  private T revenue;
}