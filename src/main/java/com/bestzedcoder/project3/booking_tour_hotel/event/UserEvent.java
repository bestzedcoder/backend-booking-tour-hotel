package com.bestzedcoder.project3.booking_tour_hotel.event;

import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import com.bestzedcoder.project3.booking_tour_hotel.redis.IRedisService;
import jakarta.persistence.PostLoad;
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
  @PostRemove
  public void onUserCreated(User user) {
    log.info("User updated/created/deleted: {} - clearing user cache...", user.getId());
    this.redisService.deleteKey("getAllUsers");
  }
}
