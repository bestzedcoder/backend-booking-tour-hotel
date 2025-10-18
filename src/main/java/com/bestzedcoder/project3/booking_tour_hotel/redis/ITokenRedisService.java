package com.bestzedcoder.project3.booking_tour_hotel.redis;

import java.time.LocalDateTime;
import java.util.Map;

public interface ITokenRedisService {
  void saveToken(Long userId,String token,String expiration);
  String getRefreshToken(Long userId);
  boolean validateBlackListToken(String token);
  void deleteToken(Long userId);
  void saveBackListToken(String token,String expiration);
}
