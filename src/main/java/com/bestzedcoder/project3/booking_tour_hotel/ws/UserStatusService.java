package com.bestzedcoder.project3.booking_tour_hotel.ws;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class UserStatusService {
  // Lưu trữ danh sách User đang online
  private final Map<String, String> onlineUsers = new ConcurrentHashMap<>();

  public void addId(String userId, String sessionId) {
    onlineUsers.put(userId, sessionId);
  }

  public void removeId(String sessionId) {
    onlineUsers.values().removeIf(id -> id.equals(sessionId));
  }

  public boolean isUserOnline(String userId) {
    return onlineUsers.containsKey(userId);
  }
}