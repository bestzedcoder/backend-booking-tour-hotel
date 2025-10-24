package com.bestzedcoder.project3.booking_tour_hotel.redis;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class redisService implements IRedisService {
  private final RedisTemplate<String, Object> redisTemplate;

  @Override
  public void saveKeyAndValue(String key, Object value, String expirationTime, TimeUnit timeUnit) {
    this.redisTemplate.opsForValue().set(key, value , Long.parseLong(expirationTime), timeUnit);
  }

  @Override
  public Object getValue(String key) {
    return this.redisTemplate.opsForValue().get(key);
  }

  @Override
  public void deleteKey(String key) {
    this.redisTemplate.delete(key);
  }
}
