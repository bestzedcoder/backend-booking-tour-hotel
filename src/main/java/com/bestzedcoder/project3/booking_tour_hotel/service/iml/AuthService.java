package com.bestzedcoder.project3.booking_tour_hotel.service.iml;

import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.LoginResponse;
import com.bestzedcoder.project3.booking_tour_hotel.exception.UnauthorizedException;
import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import com.bestzedcoder.project3.booking_tour_hotel.security.JwtUtils;
import com.bestzedcoder.project3.booking_tour_hotel.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
  private final AuthenticationManager authenticationManager;
  private final JwtUtils jwtUtils;
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


}
