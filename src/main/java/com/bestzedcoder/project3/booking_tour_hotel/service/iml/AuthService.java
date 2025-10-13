package com.bestzedcoder.project3.booking_tour_hotel.service.iml;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserSignupRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.LoginResponse;
import com.bestzedcoder.project3.booking_tour_hotel.enums.ErrorCode;
import com.bestzedcoder.project3.booking_tour_hotel.exception.BadRequestException;
import com.bestzedcoder.project3.booking_tour_hotel.exception.UnauthorizedException;
import com.bestzedcoder.project3.booking_tour_hotel.mail.IEmailService;
import com.bestzedcoder.project3.booking_tour_hotel.mail.MailDetails;
import com.bestzedcoder.project3.booking_tour_hotel.model.Role;
import com.bestzedcoder.project3.booking_tour_hotel.model.Token;
import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import com.bestzedcoder.project3.booking_tour_hotel.repository.RoleRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.UserRepository;
import com.bestzedcoder.project3.booking_tour_hotel.security.JwtUtils;
import com.bestzedcoder.project3.booking_tour_hotel.service.IAuthService;
import com.bestzedcoder.project3.booking_tour_hotel.service.ITokenService;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
  private final AuthenticationManager authenticationManager;
  private final JwtUtils jwtUtils;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final IEmailService emailService;
  private final ITokenService tokenService;
  private final RoleRepository roleRepository;

  @Value("${application.security.secretKey}")
  private String secretKey;
  @Value("${application.security.accessExpiration}")
  private String expirationTime;
  @Override
  public ApiResponse<LoginResponse> login(String username, String password) {
    Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
    Authentication authenticated = this.authenticationManager.authenticate(authentication);
    if (authenticated == null || !authenticated.isAuthenticated()) {
      throw new UnauthorizedException("Xác thực thất bại");
    }
    System.out.println(authenticated.getPrincipal());
    User user = (User) authenticated.getPrincipal();
    String access_token = this.jwtUtils.JwtGenerator(user.getUsername() , user.getAuthorities() , secretKey , expirationTime);
    LoginResponse loginResponse = new LoginResponse(access_token);
    return ApiResponse.<LoginResponse>builder().success(true).data(loginResponse).message("login success").build();
  }

  @Override
  public ApiResponse<?> register(UserSignupRequest userSignupRequest) {
    String username = userSignupRequest.getUsername();
    String password = userSignupRequest.getPassword();
    String email = userSignupRequest.getEmail();
    var checkUser = this.userRepository.findByEmail(email);
    if (checkUser != null) {
      throw new BadRequestException(ErrorCode.EMAIL_EXISTED.getMessage());
    }
    checkUser = this.userRepository.findByUsername(username);
    if (checkUser != null) {
      throw new BadRequestException(ErrorCode.USERNAME_EXISTED.getMessage());
    }

    Role role = this.roleRepository.findByName("ROLE_CUSTOMER");

    User newUser = User.builder().email(email).username(username).password(this.passwordEncoder.encode(password)).fullName("New Customer").enabled(false).roles(
        Set.of(role)).build();
    this.userRepository.save(newUser);
    String token = this.tokenService.generateAndSaveToken(newUser);
    MailDetails mailDetails = MailDetails.builder().to(newUser.getEmail()).token(token).username(newUser.getFullName()).build();
    this.emailService.sendVerificationEmail(mailDetails);
    return ApiResponse.builder()
        .success(true)
        .message("Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản trước khi đăng nhập.")
        .data(null)
        .build();
  }

  @Override
  public ApiResponse<?> verify(String code, String email) {
    boolean checkToken = this.tokenService.check(code);
    if(!checkToken) {
      throw new UnauthorizedException("Token invalid");
    }
    User user = this.userRepository.findByEmail(email);
    user.setEnabled(true);
    this.userRepository.save(user);
    return ApiResponse.builder().success(true).message("auth account success").data(Map.of("access_token" , this.jwtUtils.JwtGenerator(user.getUsername() , user.getAuthorities() , secretKey , expirationTime))).build();
  }


}
