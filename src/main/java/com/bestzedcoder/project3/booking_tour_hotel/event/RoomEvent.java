package com.bestzedcoder.project3.booking_tour_hotel.event;

import com.bestzedcoder.project3.booking_tour_hotel.model.Room;
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
public class RoomEvent {
  private final IRedisService redisService;

  @PostPersist
  @PostUpdate
  @PostRemove
  public void onHotelChanged(Room room) {
    log.info("Room updated/created/deleted: {} - clearing hotel cache...", room.getId());
    this.redisService.deleteByPattern("search:tour:info:*");
  }
}
