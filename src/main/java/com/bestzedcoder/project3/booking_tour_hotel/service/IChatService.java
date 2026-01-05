package com.bestzedcoder.project3.booking_tour_hotel.service;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.ChatMessageRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;

public interface IChatService {
  void sendMessage(ChatMessageRequest message);
  ApiResponse<?> checkUserStatus(String email);
  ApiResponse<?> getChat(Long senderId, Long receiverId);
  ApiResponse<?> getConversations(Long userId);
  ApiResponse<?> getUserSummary(Long userId);
}
