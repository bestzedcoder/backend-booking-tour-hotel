package com.bestzedcoder.project3.booking_tour_hotel.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SummaryBookingResponse {
  private long pending;
  private long cancelled;
  private long confirmed;
}
