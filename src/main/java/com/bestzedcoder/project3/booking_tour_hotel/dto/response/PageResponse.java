package com.bestzedcoder.project3.booking_tour_hotel.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> {
  private int currentPages;
  private int pageSizes;
  private int totalPages;
  private long totalElements;
  private List<T> result;
  private boolean success;
  private String message;
}
