package com.bestzedcoder.project3.booking_tour_hotel.service.iml;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.UserCreatingResponse;
import com.bestzedcoder.project3.booking_tour_hotel.enums.ErrorCode;
import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import com.bestzedcoder.project3.booking_tour_hotel.repository.UserRepository;
import com.bestzedcoder.project3.booking_tour_hotel.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService implements IUserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public ApiResponse<UserCreatingResponse> create(UserCreatingRequest request)
      throws BadRequestException {
    String username = request.getUsername();
    String password = request.getPassword();
    String email = request.getEmail();
    var checkUser = this.userRepository.findByEmail(email);
    if (checkUser != null) {
      throw new BadRequestException(ErrorCode.EMAIL_EXISTED.getMessage());
    }
    checkUser = this.userRepository.findByUsername(username);
    if (checkUser != null) {
      throw new BadRequestException(ErrorCode.USERNAME_EXISTED.getMessage());
    }
    String hashedPassword = passwordEncoder.encode(password);
    var newUser = User.builder()
        .username(username)
        .password(hashedPassword)
        .email(email)
        .enabled(true) // tam thoi test
        .fullName(request.getFullName())
        .phone(request.getPhone())
        .build();
    this.userRepository.save(newUser);
    var response = new UserCreatingResponse(
        newUser.getUsername(),
        newUser.getFullName(),
        newUser.getPassword(),
        newUser.getEmail(),
        newUser.getPhone()
    );
    return ApiResponse.<UserCreatingResponse>builder()
        .success(true)
        .message("User created")
        .data(response).build();
  }

  private String hashPassword(String password) {
    return passwordEncoder.encode(password);
  }
}
