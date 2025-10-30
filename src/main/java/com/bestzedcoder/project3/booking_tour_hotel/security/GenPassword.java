package com.bestzedcoder.project3.booking_tour_hotel.security;

import java.security.SecureRandom;

public class GenPassword {
  private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+";
  private static final int PASSWORD_LENGTH = 10;
  private static final SecureRandom RANDOM = new SecureRandom();

  public static String generateSecurePassword() {
    StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
    for (int i = 0; i < PASSWORD_LENGTH; i++) {
      password.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
    }
    return password.toString();
  }
}

