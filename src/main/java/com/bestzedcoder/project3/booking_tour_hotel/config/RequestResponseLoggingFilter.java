package com.bestzedcoder.project3.booking_tour_hotel.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component // 👈 Quan trọng: Spring sẽ tự động đăng ký filter này
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    long startTime = System.currentTimeMillis();

    // ✅ Log request (method + URL)
    log.info("➡️ Incoming Request: {} {}", request.getMethod(), request.getRequestURI());

    // Tiếp tục xử lý filter chain
    filterChain.doFilter(request, response);

    long duration = System.currentTimeMillis() - startTime;

    // ✅ Log response (status + thời gian xử lý)
    log.info("⬅️ Response: {} {} | Status: {} | Time: {} ms",
        request.getMethod(),
        request.getRequestURI(),
        response.getStatus(),
        duration);
  }
}
