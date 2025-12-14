package com.bestzedcoder.project3.booking_tour_hotel.rabbit;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingNotifier {

  // SimpMessagingTemplate được cung cấp tự động bởi Spring khi cấu hình WebSocket
  private final SimpMessagingTemplate messagingTemplate;

  public void notifyClient(Long bookingId, String status, String reason) {
    String destination = "/topic/booking/" + bookingId;

    // DTO BookingResult phải được định nghĩa để gửi thông tin
    BookingResult result = new BookingResult(bookingId, status, reason);

    // Gửi thông báo đến Client
    messagingTemplate.convertAndSend(destination, result);
  }
}
