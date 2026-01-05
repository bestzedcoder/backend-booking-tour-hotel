package com.bestzedcoder.project3.booking_tour_hotel.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversationResponse {
  private Long userId;
  private String fullName;
  private String imageUrl;
  private String lastMessage;
  private LocalDateTime timestamp;
  private boolean online;
  private int unreadCount;
}