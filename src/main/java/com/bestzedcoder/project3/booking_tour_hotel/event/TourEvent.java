package com.bestzedcoder.project3.booking_tour_hotel.event;

import com.bestzedcoder.project3.booking_tour_hotel.model.Hotel;
import com.bestzedcoder.project3.booking_tour_hotel.model.Tour;
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
public class TourEvent {
  private final IRedisService redisService;

  @PostPersist
  @PostUpdate
  @PostRemove
  public void onHotelChanged(Tour tour) {
    log.info("Tour updated/created/deleted: {} - clearing hotel cache...", tour.getId());
    this.redisService.deleteByPattern("search:tour:*");
  }
}
