package com.bestzedcoder.project3.booking_tour_hotel.event;

import com.bestzedcoder.project3.booking_tour_hotel.model.Booking;
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
public class BookingEvent {
  private final IRedisService redisService;

  @PostPersist
  @PostUpdate
  @PostRemove
  public void onBookingChanged(Booking booking) {
    log.info("Hotel updated/created/deleted: {} - clearing booking cache...", booking.getId());
    this.redisService.deleteByPattern("search:booking:*");
  }
}
