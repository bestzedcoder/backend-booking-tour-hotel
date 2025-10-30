package com.bestzedcoder.project3.booking_tour_hotel.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface IRedisService {
  <T> void saveKeyAndValue(String key, T value,String expirationTime, TimeUnit timeUnit);
  <T> T getValue(String key, TypeReference<T> typeReference);
  void deleteKey(String key);
  void deleteByPattern(String pattern);
  void clear();
}
