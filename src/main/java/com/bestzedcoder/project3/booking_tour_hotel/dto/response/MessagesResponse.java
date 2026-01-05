package com.bestzedcoder.project3.booking_tour_hotel.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessagesResponse {
  private String content;
  private Long senderId;
  private Long receiverId;
  private LocalDateTime timestamp;
}
