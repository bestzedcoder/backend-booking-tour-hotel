package com.bestzedcoder.project3.booking_tour_hotel.security;

import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import com.bestzedcoder.project3.booking_tour_hotel.redis.IRedisService;
import com.bestzedcoder.project3.booking_tour_hotel.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtValidationFilter extends OncePerRequestFilter {
  private final UserRepository userRepository;
  private final JwtUtils jwtUtils;
  private final IRedisService redisService;
  @Value("${application.security.secretKey}")
  private String secretKey;
  @Override
  protected void doFilterInternal(@NotNull HttpServletRequest request,
                                  @NotNull HttpServletResponse response,
                                  @NotNull FilterChain filterChain) throws ServletException, IOException  {
    try {
      String jwt = request.getHeader("Authorization");
      if (jwt == null || !jwt.startsWith("Bearer ")) {
        throw new BadCredentialsException("Required JWT header is missing");
      }
      String token = jwt.replace("Bearer ", "");
      Claims claims = this.jwtUtils.extractClaims(token , secretKey);
      Long userId = claims.get("userId", Long.class);
      String username = claims.get("username", String.class);
      String authorities = claims.get("authorities", String.class);
      String tokenBlackList = this.redisService.getValue("BlackList:"+token+userId, new TypeReference<String>() {});
      if(tokenBlackList != null && tokenBlackList.equals(token)) {
        throw new BadCredentialsException("Token has been revoked. Please login again.");
      }
      User user = this.userRepository.findByUsername(username);
      Authentication authentication = new UsernamePasswordAuthenticationToken(user ,null ,
          AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      filterChain.doFilter(request, response);
    } catch (BadCredentialsException ex) {
      handleException(response, HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

  }

  private void handleException(HttpServletResponse response, HttpStatus status, String message) throws IOException {
    response.setStatus(status.value());
    response.setContentType("application/json;charset=UTF-8");

    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now().toString());
    body.put("status", status.value());
    body.put("error", status.getReasonPhrase());
    body.put("message", message);
    body.put("path", "/auth/verify");
    body.put("success", false);

    new ObjectMapper().writeValue(response.getOutputStream(), body);
  }


  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String requestURI = request.getRequestURI();
    return requestURI.contains("/register") || requestURI.contains("/login") || requestURI.contains("/verify") || requestURI.contains("/refresh") || requestURI.contains("/swagger-ui") || requestURI.contains("/v3/api-docs");
  }
}
