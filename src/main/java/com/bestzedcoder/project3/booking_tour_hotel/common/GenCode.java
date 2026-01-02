package com.bestzedcoder.project3.booking_tour_hotel.common;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class GenCode {
  private static final String PREFIX = "ORD";
  private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final String NUMBERS = "0123456789";
  private static final int RANDOM_LENGTH = 5;
  private static final SecureRandom RANDOM = new SecureRandom();
  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

  public static String generateOrderCode() {
    String timePart = LocalDateTime.now().format(FORMATTER);
    String randomPart = randomString(RANDOM_LENGTH, CHARACTERS);
    return PREFIX + "-" + timePart + "-" + randomPart;
  }

  public static String generateRandomNumber(int length) {
    return randomString(length, NUMBERS);
  }

  private static String randomString(int length, String characters) {
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      int index = RANDOM.nextInt(characters.length());
      sb.append(characters.charAt(index));
    }
    return sb.toString();
  }
}
