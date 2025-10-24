package com.bestzedcoder.project3.booking_tour_hotel.redis;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface IRedisService {
  void saveKeyAndValue(String key, Object value,String expirationTime, TimeUnit timeUnit);
  Object getValue(String key);
  void deleteKey(String key);
}
