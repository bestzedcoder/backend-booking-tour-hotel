package com.bestzedcoder.project3.booking_tour_hotel.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {
    // Populate dynamic values
    LocalDateTime currentTimeStamp = LocalDateTime.now();
    String message = (accessDeniedException != null && accessDeniedException.getMessage() != null) ? accessDeniedException.getMessage() : "Unauthorized";
    String path = request.getRequestURI();
    response.setHeader("error-reason" , "Authorization Failed");
    response.setStatus(HttpStatus.FORBIDDEN.value());
    response.setContentType("application/json;charset=UTF-8");
    // Construct the JSON response
    String jsonResponse =
        String.format("{\"timestamp\":\"%s\",\"success\":%b,\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\"}",
            currentTimeStamp, false , HttpStatus.FORBIDDEN.getReasonPhrase(), message , path );
    response.getWriter().write(jsonResponse);
  }
}

