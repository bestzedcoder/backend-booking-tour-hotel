package com.bestzedcoder.project3.booking_tour_hotel.redis;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenRedisService implements ITokenRedisService {

  private final RedisTemplate<String, Object> redisTemplate;
  @Override
  public void saveToken(Long userId, String token, String expiration) {
    this.redisTemplate.opsForValue().set("refresh:" + userId,token , Long.parseLong(expiration) , TimeUnit.SECONDS);
  }

  @Override
  public String getRefreshToken(Long userId) {
    return (String) this.redisTemplate.opsForValue().get("refresh:"+userId);
  }

  @Override
  public boolean validateBlackListToken(String token) {
    String checkToken = (String) this.redisTemplate.opsForValue().get("blacklist:"+token);
    return checkToken == null || !checkToken.equals("ok");
  }

  @Override
  public void deleteToken(Long userId) {
    this.redisTemplate.delete("refresh:"+userId);
  }

  @Override
  public void saveBackListToken(String token, String expiration) {
    this.redisTemplate.opsForValue().set("backlist:"+token , "ok" , Long.parseLong(expiration) , TimeUnit.SECONDS);
  }
}
