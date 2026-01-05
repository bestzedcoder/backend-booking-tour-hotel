package com.bestzedcoder.project3.booking_tour_hotel.ws;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

  private final UserStatusService userStatusService;

  @EventListener
  public void handleWebSocketConnectListener(SessionConnectEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    // Lấy userId từ Header lúc handshake (hoặc từ principal)
    String userId = headerAccessor.getFirstNativeHeader("userId");
    String sessionId = headerAccessor.getSessionId();

    if (userId != null) {
      userStatusService.addId(userId, sessionId);
    }
  }

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    String sessionId = event.getSessionId();
    userStatusService.removeId(sessionId);
  }
}