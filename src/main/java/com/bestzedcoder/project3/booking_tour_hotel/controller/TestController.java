package com.bestzedcoder.project3.booking_tour_hotel.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {
  @GetMapping("rate-limiting")
  public String rateLimiting() {
    return "Rate limiting";
  }
}
