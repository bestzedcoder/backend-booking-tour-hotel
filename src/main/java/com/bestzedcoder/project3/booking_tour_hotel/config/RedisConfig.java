package com.bestzedcoder.project3.booking_tour_hotel.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
  @Value("${spring.data.redis.host}")
  private String redisHost;
  @Value("${spring.data.redis.port}")
  private int redisPort;

  @Bean
  public LettuceConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
    return new LettuceConnectionFactory(config);
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory());
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));

    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
    template.afterPropertiesSet();
    return template;
  }

  @Bean
  public ObjectMapper objectMapper() {
    // convert : [2025, 10, 16, 22, 35, 0] => "2025-10-16T22:35:00"
    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME));
    module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME));
    mapper.registerModule(module);
    return mapper;
  }
}
