package com.bestzedcoder.project3.booking_tour_hotel.controller;

import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.service.IPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("payment")
@RequiredArgsConstructor
public class PaymentController {
  private final IPaymentService paymentService;

  @GetMapping("/vn-pay/{bookingId}")
  public ResponseEntity<ApiResponse<?>> pay(
      @PathVariable Long bookingId,
      HttpServletRequest request) {

    ApiResponse<?> response = paymentService.createPayment(bookingId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/vn-pay-callback")
  public void callback(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    Map<String, String> vnPayResponse = request.getParameterMap()
        .entrySet()
        .stream()
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            e -> e.getValue()[0]
        ));
    String url = this.paymentService.handleVNPayCallback(vnPayResponse);
    response.sendRedirect(url);
  }
}
