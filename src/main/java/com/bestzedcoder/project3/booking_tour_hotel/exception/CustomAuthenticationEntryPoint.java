package com.bestzedcoder.project3.booking_tour_hotel.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException, ServletException {
    LocalDateTime currentTimeStamp = LocalDateTime.now();
    String message = (authException != null && authException.getMessage() != null) ? authException.getMessage() : "Unauthorized";
    String path = request.getRequestURI();
    response.setHeader("error-reason" , "Authentication Failed");
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType("application/json;charset=UTF-8");
    // Construct the JSON response
    String jsonResponse =
        String.format("{\"timestamp\":\"%s\",\"success\":%b,\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\"}",
            currentTimeStamp, false , HttpStatus.UNAUTHORIZED.getReasonPhrase(), message , path );
    response.getWriter().write(jsonResponse);
  }
}
