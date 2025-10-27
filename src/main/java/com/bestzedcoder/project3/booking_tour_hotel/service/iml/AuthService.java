package com.bestzedcoder.project3.booking_tour_hotel.service.iml;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.RefreshTokenReqest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserSignupRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.LoginResponse;
import com.bestzedcoder.project3.booking_tour_hotel.enums.ErrorCode;
import com.bestzedcoder.project3.booking_tour_hotel.exception.BadRequestException;
import com.bestzedcoder.project3.booking_tour_hotel.exception.UnauthorizedException;
import com.bestzedcoder.project3.booking_tour_hotel.mail.IEmailService;
import com.bestzedcoder.project3.booking_tour_hotel.mail.MailDetails;
import com.bestzedcoder.project3.booking_tour_hotel.model.Profile;
import com.bestzedcoder.project3.booking_tour_hotel.model.Role;
import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import com.bestzedcoder.project3.booking_tour_hotel.redis.IRedisService;
import com.bestzedcoder.project3.booking_tour_hotel.repository.ProfileRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.RoleRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.UserRepository;
import com.bestzedcoder.project3.booking_tour_hotel.security.JwtUtils;
import com.bestzedcoder.project3.booking_tour_hotel.service.IAuthService;
import com.bestzedcoder.project3.booking_tour_hotel.service.ITokenService;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
  private final ProfileRepository profileRepository;
  private final IRedisService redisService;

  @Value("${application.security.secretKey}")
  private String secretKey;
  @Value("${application.security.accessExpiration}")
  private String expirationTimeAccess;
  @Value("${application.security.refreshExpiration}")
  private String expirationTimeRefresh;
  @Override
  public ApiResponse<LoginResponse> login(String username, String password) {
    Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
    Authentication authenticated = this.authenticationManager.authenticate(authentication);
    if (authenticated == null || !authenticated.isAuthenticated()) {
      throw new UnauthorizedException("Xác thực thất bại");
    }
    System.out.println(authenticated.getPrincipal());
    User user = (User) authenticated.getPrincipal();
    String access_token = this.jwtUtils.JwtGenerator(user, secretKey , expirationTimeAccess);
    String refresh_token = this.jwtUtils.JwtGenerator(user, secretKey , expirationTimeRefresh);
    this.redisService.saveKeyAndValue("accessToken:"+user.getId() , access_token , expirationTimeAccess , TimeUnit.SECONDS);
    this.redisService.saveKeyAndValue("refreshToken:"+user.getId() , refresh_token , expirationTimeRefresh , TimeUnit.SECONDS);
    LoginResponse loginResponse = new LoginResponse(user.getId(),user.getUsername() , user.getProfile() , access_token, refresh_token ,  user.getUpdateProfile());
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
    Profile profile = Profile.builder().fullName("New Customer").build();
    User newUser = User.builder().email(email).username(username).password(this.passwordEncoder.encode(password)).profile(profile).enabled(false).updateProfile(false).roles(
        Set.of(role)).build();
    profile.setUser(newUser);
    this.userRepository.save(newUser);
    String token = this.tokenService.generateAndSaveToken(newUser);
    MailDetails mailDetails = MailDetails.builder().to(newUser.getEmail()).token(token).username(newUser.getUsername()).build();
    this.emailService.sendVerificationEmail(mailDetails);
    return ApiResponse.builder()
        .success(true)
        .message("Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản trước khi đăng nhập.")
        .build();
  }

  @Override
  public ApiResponse<?> verify(String code, String email) {
    boolean checkToken = this.tokenService.check(code);
    if(!checkToken) {
      throw new UnauthorizedException("Token hết hạn vui lòng liên hệ với nhà quản trị");
    }
    User user = this.userRepository.findByEmail(email);
    user.setEnabled(true);
    user.setUpdateProfile(false);
    this.userRepository.save(user);
    return ApiResponse.builder().success(true).message("Verify success.").build();
  }

  @Override
  public ApiResponse<?> refresh(RefreshTokenReqest refreshTokenReqest) {
    Long userId = refreshTokenReqest.getUserId();
    String refreshToken = refreshTokenReqest.getRefreshToken();
    String refreshRedis = this.redisService.getValue("refreshToken:"+userId,new TypeReference<String>() {});
    if(refreshRedis == null) throw new UnauthorizedException("Refresh token expired");
    else if(!refreshRedis.equals(refreshToken)) {
      throw new UnauthorizedException("Refresh token invalid");
    }
    User user = this.userRepository.findById(userId).orElseThrow(() -> new BadRequestException(
        "User not found"));
    String accessTokenNew = this.jwtUtils.JwtGenerator(user, secretKey , expirationTimeAccess);
    this.redisService.saveKeyAndValue("accessToken:"+user.getId() , accessTokenNew , expirationTimeAccess , TimeUnit.SECONDS);
    return ApiResponse.builder().success(true).message("access_token new").data(Map.of("access_token" , accessTokenNew)).build();
  }

  @Override
  public ApiResponse<?> logout() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = (User) authentication.getPrincipal();
    String accessToken = (String) this.redisService.getValue("accessToken:"+user.getId(),new TypeReference<String>() {});
    this.redisService.saveKeyAndValue("BlackList:"+accessToken+user.getId() , accessToken , expirationTimeAccess , TimeUnit.SECONDS);
    this.redisService.deleteKey("accessToken:"+user.getId());
    this.redisService.deleteKey("refreshToken:"+user.getId());
    return ApiResponse.builder().success(true).message("logout success").build();
  }

}
