package com.bestzedcoder.project3.booking_tour_hotel.security;

import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import com.bestzedcoder.project3.booking_tour_hotel.redis.IRedisService;
import com.bestzedcoder.project3.booking_tour_hotel.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
  protected void doFilterInternal(@NotNull HttpServletRequest request,@NotNull HttpServletResponse response,
     @NotNull FilterChain filterChain) throws ServletException, IOException  {
    System.out.println("secretKey: " + secretKey);
    String jwt = request.getHeader("Authorization");
    if (jwt == null || !jwt.startsWith("Bearer ")) {
      throw new BadCredentialsException("Required JWT header is missing");
    }
    String token = jwt.replace("Bearer ", "");
    Claims claims = this.jwtUtils.extractClaims(token , secretKey);
    Long userId = claims.get("userId", Long.class);
    String username = claims.get("username", String.class);
    String authorities = claims.get("authorities", String.class);
    String tokenBlackList = (String) this.redisService.getValue("BlackList:"+token+userId);
    if(tokenBlackList != null && tokenBlackList.equals(token)) {
      throw new BadCredentialsException("Token has been revoked. Please login again.");
    }
    User user = this.userRepository.findByUsername(username);
    Authentication authentication = new UsernamePasswordAuthenticationToken(user ,null ,
        AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    filterChain.doFilter(request, response);
  }

  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String requestURI = request.getRequestURI();
    return requestURI.contains("/register") || requestURI.contains("/login") || requestURI.contains("/verify") || requestURI.contains("/refresh");
  }
}
