package com.bestzedcoder.project3.booking_tour_hotel.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
  public static final String BOOKING_QUEUE = "booking.queue";
  public static final String EMAIL_QUEUE = "email.queue";

  @Bean
  public Queue bookingQueue() {
    return new Queue(BOOKING_QUEUE, true);
  }

  @Bean
  public Queue emailQueue() { return new Queue(EMAIL_QUEUE, true); }

  @Bean
  public Jackson2JsonMessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public RabbitTemplate rabbitTemplate(
      ConnectionFactory connectionFactory,
      Jackson2JsonMessageConverter messageConverter) {

    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(messageConverter);
    return template;
  }
}
