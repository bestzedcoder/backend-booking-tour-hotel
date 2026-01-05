package com.bestzedcoder.project3.booking_tour_hotel.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
public class ChatMessageRequest {
  private String senderId;
  private String receiverId;
  private String content;
  private String timestamp;
}
