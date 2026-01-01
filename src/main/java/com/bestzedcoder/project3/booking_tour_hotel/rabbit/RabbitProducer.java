package com.bestzedcoder.project3.booking_tour_hotel.rabbit;

import com.bestzedcoder.project3.booking_tour_hotel.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitProducer {

  private final RabbitTemplate rabbitTemplate;

  public void sendBooking(BookingMessage message) {
    rabbitTemplate.convertAndSend(
        RabbitConfig.BOOKING_QUEUE,
        message
    );
  }

  public void sendEmail(EmailMessage message) {
    rabbitTemplate.convertAndSend(
        RabbitConfig.EMAIL_QUEUE,
        message
    );
  }
}

