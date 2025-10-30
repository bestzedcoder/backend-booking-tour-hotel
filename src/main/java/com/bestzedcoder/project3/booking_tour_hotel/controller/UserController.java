package com.bestzedcoder.project3.booking_tour_hotel.controller;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserUpdatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.PageResponse;
import com.bestzedcoder.project3.booking_tour_hotel.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
  private final IUserService userService;

  @PostMapping
  public ResponseEntity<ApiResponse<?>> create(@RequestBody @Valid
      UserCreatingRequest request) throws BadRequestException {
      log.info("Creating user: {}", request);
      ApiResponse<?> response = this.userService.create(request);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<?>> findById(@PathVariable("id") Long id) {
    log.info("Find user by id: {}", id);
    ApiResponse<?> response = this.userService.getUserById(id);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping
  public ResponseEntity<PageResponse<?>> getAllUsers(@RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(defaultValue = "10") int limit) {
    PageResponse<?> response = this.userService.getAllUsers(page,limit);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PutMapping(value = "/{id}" ,consumes = {"multipart/form-data"})
  public  ResponseEntity<ApiResponse<?>> update(@PathVariable("id") Long id,
                                                @RequestPart("data") @Valid UserUpdatingRequest request,
                                                @RequestPart(value = "image" , required = false)  MultipartFile image) {
    log.info("Updating user: {}", request);
    ApiResponse<?> response = this.userService.updateUserById(id,request,image);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<?>> delete(@PathVariable("id") Long id) {
    log.info("Deleting user: {}", id);
    ApiResponse<?> response = this.userService.deleteUserById(id);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
