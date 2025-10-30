package com.bestzedcoder.project3.booking_tour_hotel.exception;

import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.exception.BadRequestException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalHandlingException {

  /**
   * Xử lý các lỗi thông thường (tổng quát)
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGlobalException(Exception exception) {
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    String message = exception.getMessage();

    if (exception instanceof BadRequestException) {
      status = HttpStatus.BAD_REQUEST;
    } else if (exception instanceof UsernameNotFoundException) {
      status = HttpStatus.NOT_FOUND;
      message = "Không tìm thấy người dùng.";
    } else if (exception instanceof AccessDeniedException) {
      status = HttpStatus.FORBIDDEN;
      message = "Bạn không có quyền truy cập tài nguyên này.";
    } else if (exception instanceof ResponseStatusException ex) {
      status = (HttpStatus) ex.getStatusCode();
      message = ex.getReason() != null ? ex.getReason() : "Lỗi yêu cầu.";
    } else if (exception instanceof BadCredentialsException) {
      status = HttpStatus.UNAUTHORIZED;
      message = "Tên đăng nhập hoặc mật khẩu không chính xác.";
    } else if (exception instanceof IllegalArgumentException) {
      status = HttpStatus.BAD_REQUEST;
    } else if (exception instanceof NullPointerException) {
      message = "Lỗi NullPointer — dữ liệu không hợp lệ.";
    }

    // Log nếu cần
    exception.printStackTrace();

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("success", false);
    body.put("timestamp", LocalDateTime.now().toString());
    body.put("status", status.value());
    body.put("error", status.getReasonPhrase());
    body.put("message", message);

    return new ResponseEntity<>(body, status);
  }

  /**
   * Lỗi validate @Valid
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException e) {
    Map<String, String> errors = new HashMap<>();
    e.getBindingResult().getFieldErrors().forEach(err ->
        errors.put(err.getField(), err.getDefaultMessage())
    );

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("success", false);
    body.put("timestamp", LocalDateTime.now().toString());
    body.put("status", HttpStatus.BAD_REQUEST.value());
    body.put("error", "Validation Failed");
    body.put("message", errors);

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  /**
   * Lỗi ResourceNotFoundException
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException e) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("success", false);
    body.put("timestamp", LocalDateTime.now().toString());
    body.put("status", HttpStatus.NOT_FOUND.value());
    body.put("error", "Not Found");
    body.put("message", e.getMessage());

    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
  }
}