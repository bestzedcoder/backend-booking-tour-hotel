package com.bestzedcoder.project3.booking_tour_hotel.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class redisService implements IRedisService {
  private final RedisTemplate<String, Object> redisTemplate;
  private final ObjectMapper objectMapper;

  @Override
  public <T> void saveKeyAndValue(String key, T value, String expirationTime, TimeUnit timeUnit) {
    try {
      this.redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(value) , Long.parseLong(expirationTime), timeUnit);
    }  catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public <T> T getValue(String key, TypeReference<T> typeReference) {
    String json = (String) this.redisTemplate.opsForValue().get(key);
    if (json == null) return null;
    try {
      return objectMapper.readValue(json, typeReference);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to deserialize JSON from Redis", e);
    }
  }

  @Override
  public void deleteKey(String key) {
    this.redisTemplate.delete(key);
  }

  @Override
  public void deleteByPattern(String pattern) {
    Set<String> keys = this.redisTemplate.keys(pattern);
    if (keys != null && keys.size() > 0) {
      this.redisTemplate.delete(keys);
    }
  }

  @Override
  public void clear() {
    this.redisTemplate.getConnectionFactory().getConnection().flushAll();
  }
}
