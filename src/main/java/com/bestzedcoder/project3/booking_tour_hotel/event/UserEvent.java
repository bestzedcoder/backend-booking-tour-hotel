package com.bestzedcoder.project3.booking_tour_hotel.event;

import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import com.bestzedcoder.project3.booking_tour_hotel.redis.IRedisService;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserEvent {
  private final IRedisService redisService;

  @PostUpdate
  @PostPersist
  public void onUserCreatedOrUpdated(User user) {
    log.info("User updated/created/deleted: {} - clearing user cache...", user.getId());
    this.redisService.deleteByPattern("search:users:*");
  }
  @PostRemove
  public void onUserDeleted(User user) {
    log.info("User deleted: {} - clearing user cache...", user.getId());
    this.redisService.deleteByPattern("search:users:*");
    this.redisService.deleteKey("auth:accessToken:" + user.getId());
    this.redisService.deleteKey("auth:refreshToken:" + user.getId());
  }

}
