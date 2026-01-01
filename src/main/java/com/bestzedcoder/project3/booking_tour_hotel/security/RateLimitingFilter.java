package com.bestzedcoder.project3.booking_tour_hotel.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

  // lưu trữ "xô" cho từng IP
  private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

  // 10 yêu cầu trong vòng 1 phút
  private Bucket createNewBucket() {
    return Bucket.builder()
        .addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1))))
        .build();
  }

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  protected void doFilterInternal(
      @NotNull HttpServletRequest request,
      @NotNull HttpServletResponse response,
      @NotNull FilterChain filterChain) throws ServletException, IOException {
    // IP request
    String clientIp = request.getRemoteAddr();

    // Lấy xô tương ứng với IP, nếu chưa có thì tạo mới
    Bucket bucket = buckets.computeIfAbsent(clientIp, k -> createNewBucket());

    if (bucket.tryConsume(1)) {
      filterChain.doFilter(request, response);
    } else {
      response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");

      Map<String, Object> body = new LinkedHashMap<>();
      body.put("success", false);
      body.put("timestamp", LocalDateTime.now().toString());
      body.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
      body.put("message", "Bạn đã gửi quá nhiều yêu cầu. Vui lòng thử lại sau 1 phút.");

      String jsonResponse = objectMapper.writeValueAsString(body);
      response.getWriter().write(jsonResponse);
    }

  }
}
