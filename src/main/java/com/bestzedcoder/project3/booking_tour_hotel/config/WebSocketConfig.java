package com.bestzedcoder.project3.booking_tour_hotel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Kích hoạt tính năng xử lý tin nhắn WebSocket với Broker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  /**
   * Cấu hình Message Broker
   */
  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    // 1. Điểm đến (Destination Prefix) mà Server sẽ đẩy thông báo đi (Dùng cho Broker)
    // Đây là prefix mà BookingNotifier sẽ sử dụng (ví dụ: /topic/booking/123)
    config.enableSimpleBroker("/topic");

    // 2. Điểm đến mà Client gửi tin nhắn lên Server (Nếu có, ví dụ: /app/chat)
    // Hiện tại không cần dùng, nhưng nên định nghĩa
    config.setApplicationDestinationPrefixes("/app");
  }

  /**
   * Đăng ký Stomp Endpoints (Điểm cuối kết nối WebSocket)
   */
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // Điểm cuối để Client React kết nối: ws://localhost:8080/ws-booking
    // Dùng withSockJS() để hỗ trợ các trình duyệt cũ hơn
    registry.addEndpoint("/ws-booking")
        .setAllowedOriginPatterns("http://localhost:5174", "http://127.0.0.1:5174","http://localhost:5173", "http://127.0.0.1:5173");;
  }
}
