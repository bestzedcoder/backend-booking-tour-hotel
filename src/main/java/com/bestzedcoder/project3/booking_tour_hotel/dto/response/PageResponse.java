package com.bestzedcoder.project3.booking_tour_hotel.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {

  private int currentPages;
  private int pageSizes;
  private int totalPages;
  private long totalElements;
  private List<T> result;
}
