package com.bestzedcoder.project3.booking_tour_hotel.service;

import com.bestzedcoder.project3.booking_tour_hotel.model.User;

public interface ITokenService {
  String generateAndSaveToken(User user);
  boolean check(String token);
}
