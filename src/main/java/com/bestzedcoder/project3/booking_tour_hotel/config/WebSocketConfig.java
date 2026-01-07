package com.bestzedcoder.project3.booking_tour_hotel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  /**
   * Cấu hình Message Broker
   */
  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic");
    config.setApplicationDestinationPrefixes("/app");
  }

  /**
   * Đăng ký Stomp Endpoints (Điểm cuối kết nối WebSocket)
   */
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws-booking")
        .setAllowedOriginPatterns("http://localhost:5174", "http://127.0.0.1:5174","http://localhost:5173", "http://127.0.0.1:5173" , "http://160.30.172.199:5173" , "https://frontend-booking-hotel-tour.onrender.com");

    registry.addEndpoint("/ws-chat")
        .setAllowedOriginPatterns("http://localhost:5174", "http://127.0.0.1:5174","http://localhost:5173", "http://127.0.0.1:5173", "http://160.30.172.199:5173" , "https://frontend-booking-hotel-tour.onrender.com");
  }
}
