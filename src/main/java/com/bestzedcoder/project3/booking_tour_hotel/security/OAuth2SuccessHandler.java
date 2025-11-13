package com.bestzedcoder.project3.booking_tour_hotel.security;

import com.bestzedcoder.project3.booking_tour_hotel.exception.ResourceNotFoundException;
import com.bestzedcoder.project3.booking_tour_hotel.model.Profile;
import com.bestzedcoder.project3.booking_tour_hotel.model.Role;
import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import com.bestzedcoder.project3.booking_tour_hotel.redis.IRedisService;
import com.bestzedcoder.project3.booking_tour_hotel.repository.RoleRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.UserRepository;
import com.bestzedcoder.project3.booking_tour_hotel.service.IAuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
  private final IRedisService redisService;
  private final JwtUtils jwtUtils;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final RoleRepository roleRepository;

  @Value("${application.security.secretKey}")
  private String secretKey;
  @Value("${application.security.accessExpiration}")
  private String expirationTimeAccess;
  @Value("${application.security.refreshExpiration}")
  private String expirationTimeRefresh;
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    String email = oAuth2User.getAttribute("email");
    Map<String , String> auth = this.solve(email);
    response.sendRedirect("http://localhost:5173/oauth2/success?accessToken=" + auth.get("accessToken") + "&refreshToken=" + auth.get("refreshToken"));
  }

  private Map<String, String> solve(String email) {
    User user = Optional.ofNullable(userRepository.findByEmail(email))
        .orElseGet(() -> createUser(email));

    // Tạo token
    String accessToken = this.jwtUtils.JwtGenerator(user, secretKey , expirationTimeAccess);
    String refreshToken = this.jwtUtils.JwtGenerator(user, secretKey , expirationTimeRefresh);
    this.redisService.saveKeyAndValue("auth:accessToken:"+user.getId() , accessToken , expirationTimeAccess , TimeUnit.SECONDS);
    this.redisService.saveKeyAndValue("auth:refreshToken:"+user.getId() , refreshToken, expirationTimeRefresh , TimeUnit.SECONDS);

    return Map.of(
        "accessToken", accessToken,
        "refreshToken", refreshToken
    );
  }

  private User createUser(String email) {
    User user = User.builder()
        .username(email)
        .email(email)
        .password(this.passwordEncoder.encode(UUID.randomUUID().toString()))
        .enabled(true)
        .updateProfile(false)
        .build();

    // Nếu muốn tạo profile mặc định
    Profile profile = new Profile();
    profile.setFullName(email); // hoặc từ Google name
    profile.setUser(user);
    user.setProfile(profile);

    // Set role mặc định
    Role role = this.roleRepository.findByName("ROLE_CUSTOMER").orElseThrow(() -> new ResourceNotFoundException("Role customer not found"));
    user.setRoles(Set.of(role));
    return userRepository.save(user);
  }
}
