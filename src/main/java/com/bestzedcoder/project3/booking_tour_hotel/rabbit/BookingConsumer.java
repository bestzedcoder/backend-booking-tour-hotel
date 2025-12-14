package com.bestzedcoder.project3.booking_tour_hotel.rabbit;

import com.bestzedcoder.project3.booking_tour_hotel.config.RabbitConfig;
import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingType;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingConsumer {

  private final BookingProcessor bookingProcessor;

  @RabbitListener(
      queues = RabbitConfig.BOOKING_QUEUE,
      concurrency = "1"
  )
  public void consume(BookingMessage msg) {

    if (msg.getBookingType().equals(BookingType.HOTEL)) {
      bookingProcessor.processHotel(msg);
    } else {
      bookingProcessor.processTour(msg);
    }
  }
}

