package com.bestzedcoder.project3.booking_tour_hotel.rabbit;

import com.bestzedcoder.project3.booking_tour_hotel.config.RabbitConfig;
import com.bestzedcoder.project3.booking_tour_hotel.enums.BookingType;
import com.bestzedcoder.project3.booking_tour_hotel.exception.BadRequestException;
import com.bestzedcoder.project3.booking_tour_hotel.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingConsumer {

  private final BookingProcessor bookingProcessor;

  @RabbitListener(
      queues = RabbitConfig.BOOKING_QUEUE,
      concurrency = "2"
  )
  public void consume(BookingMessage msg) {

    try {
      Thread.sleep(2000);
      if (msg.getBookingType().equals(BookingType.HOTEL)) {
        bookingProcessor.processHotel(msg);
      } else {
        bookingProcessor.processTour(msg);
      }

    } catch (BadRequestException | ResourceNotFoundException e) {
      bookingProcessor.handleBookingFailed(msg.getBookingCode(), e.getMessage());
      throw new AmqpRejectAndDontRequeueException("Business Error: " + e.getMessage(), e);

    } catch (Exception e) {
      throw new AmqpException("Transient Error. Retrying...", e);
    }
  }
}

