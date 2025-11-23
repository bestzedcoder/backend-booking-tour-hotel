package com.bestzedcoder.project3.booking_tour_hotel.service;

import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

public interface IPaymentService {
  ApiResponse<?> createPayment(Long bookingId, HttpServletRequest request);
  String handleVNPayCallback(Map<String, String> vnPayResponse);
}
