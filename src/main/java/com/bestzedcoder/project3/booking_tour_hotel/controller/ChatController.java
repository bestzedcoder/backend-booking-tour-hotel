package com.bestzedcoder.project3.booking_tour_hotel.controller;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.ChatMessageRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.service.IChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("chat")
public class ChatController {

  private final IChatService chatService;

  @MessageMapping("/chat.sendMessage")
  public void sendMessage(@Payload ChatMessageRequest message) {
    this.chatService.sendMessage(message);
  }


  @GetMapping("/check-status")
  public ResponseEntity<ApiResponse<?>> searchUser(@RequestParam String email) {
    ApiResponse<?> response = this.chatService.checkUserStatus(email);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{senderId}/to/{receiverId}")
  public ResponseEntity<ApiResponse<?>> getChat(@PathVariable Long senderId, @PathVariable Long receiverId) {
    ApiResponse<?> response = this.chatService.getChat(senderId ,receiverId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/conversations/{userId}")
  public ResponseEntity<ApiResponse<?>> getConversations(@PathVariable Long userId) {
    return ResponseEntity.ok(this.chatService.getConversations(userId));
  }


  @GetMapping("/summary/{userId}")
  public ResponseEntity<ApiResponse<?>> getUserSummary(@PathVariable Long userId) {
    return ResponseEntity.ok(this.chatService.getUserSummary(userId));
  }
}
