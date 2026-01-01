package com.bestzedcoder.project3.booking_tour_hotel.rabbit;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingNotifier {

  private final SimpMessagingTemplate messagingTemplate;

  public void notifyClient(String bookingCode, String bookingType, String status, String reason) {
    String destination = String.format("/topic/booking/%s/type/%s", bookingCode, bookingType);

    BookingResult result = new BookingResult(bookingCode, status, reason);

    messagingTemplate.convertAndSend(destination, result);
  }
}
