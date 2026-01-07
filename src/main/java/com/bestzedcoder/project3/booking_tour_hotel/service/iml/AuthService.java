package com.bestzedcoder.project3.booking_tour_hotel.service.iml;

import com.bestzedcoder.project3.booking_tour_hotel.common.CookieUtils;
import com.bestzedcoder.project3.booking_tour_hotel.common.GenCode;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.ChangePasswordRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.RefreshTokenReqest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserSignupRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.enums.EmailType;
import com.bestzedcoder.project3.booking_tour_hotel.exception.BadRequestException;
import com.bestzedcoder.project3.booking_tour_hotel.exception.ResourceNotFoundException;
import com.bestzedcoder.project3.booking_tour_hotel.exception.UnauthorizedException;
import com.bestzedcoder.project3.booking_tour_hotel.mail.IEmailService;
import com.bestzedcoder.project3.booking_tour_hotel.mail.MailDetails;
import com.bestzedcoder.project3.booking_tour_hotel.mapper.UserMapper;
import com.bestzedcoder.project3.booking_tour_hotel.model.Profile;
import com.bestzedcoder.project3.booking_tour_hotel.model.Role;
import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import com.bestzedcoder.project3.booking_tour_hotel.rabbit.EmailMessage;
import com.bestzedcoder.project3.booking_tour_hotel.rabbit.RabbitProducer;
import com.bestzedcoder.project3.booking_tour_hotel.redis.IRedisService;
import com.bestzedcoder.project3.booking_tour_hotel.repository.RoleRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.UserRepository;
import com.bestzedcoder.project3.booking_tour_hotel.security.GenPassword;
import com.bestzedcoder.project3.booking_tour_hotel.security.JwtUtils;
import com.bestzedcoder.project3.booking_tour_hotel.service.IAuthService;
import com.bestzedcoder.project3.booking_tour_hotel.service.ITokenService;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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
//  private final IEmailService emailService;
  private final ITokenService tokenService;
  private final RoleRepository roleRepository;
  private final IRedisService redisService;
  private final CookieUtils cookieUtils;
  private final RabbitProducer rabbitProducer;

  @Value("${application.security.secretKey}")
  private String secretKey;
  @Value("${application.security.accessExpiration}")
  private String expirationTimeAccess;
  @Value("${application.security.refreshExpiration}")
  private String expirationTimeRefresh;
  @Override
  public ApiResponse<?> login(String username, String password, HttpServletResponse res) {
    Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
    Authentication authenticated = this.authenticationManager.authenticate(authentication);
    if (authenticated == null || !authenticated.isAuthenticated()) {
      throw new UnauthorizedException("Xác thực thất bại");
    }
    User user = (User) authenticated.getPrincipal();
    String access_token = this.jwtUtils.JwtGenerator(user, secretKey , expirationTimeAccess);
    String refresh_token = this.jwtUtils.JwtGenerator(user, secretKey , expirationTimeRefresh);
    this.redisService.saveKeyAndValue("auth:accessToken:"+user.getId() , access_token , expirationTimeAccess , TimeUnit.SECONDS);
    this.redisService.saveKeyAndValue("auth:refreshToken:"+user.getId() , refresh_token , expirationTimeRefresh , TimeUnit.SECONDS);
    ResponseCookie cookie =  ResponseCookie.from("refresh_token" , refresh_token)
        .maxAge(Long.parseLong(this.expirationTimeRefresh))
        .secure(true)
        .httpOnly(true)
        .path("/")
        .sameSite("None")
        .build();
    res.setHeader(HttpHeaders.SET_COOKIE , cookie.toString());
    return ApiResponse.builder().success(true).data(Map.of("access_token" , access_token)).message("login success").build();
  }

  @Override
  public ApiResponse<?> register(UserSignupRequest userSignupRequest) {
    String username = userSignupRequest.getUsername();
    String password = userSignupRequest.getPassword();
    String email = userSignupRequest.getEmail();
    if (userRepository.existsByUsername(username)) {
      throw new BadRequestException("Username đã tồn tại");
    }
    if (userRepository.existsByEmail(email)) {
      throw new BadRequestException("Email đã tồn tại");
    }

    Role role = this.roleRepository.findByName("ROLE_CUSTOMER").orElseThrow(() -> new ResourceNotFoundException("Role customer not found"));
    Profile profile = Profile.builder().fullName("New Customer").build();
    User newUser = User.builder().email(email).username(username).password(this.passwordEncoder.encode(password)).profile(profile).enabled(false).updateProfile(false).roles(
        Set.of(role)).build();
    profile.setUser(newUser);
    this.userRepository.save(newUser);
    String token = this.tokenService.generateAndSaveToken(newUser);
    MailDetails mailDetails = MailDetails.builder().to(newUser.getEmail()).token(token).username(newUser.getUsername()).build();
    EmailMessage emailMessage = new EmailMessage();
    emailMessage.setMessageType(EmailType.CODE_VERIFY);
    emailMessage.setMailDetails(mailDetails);
    this.rabbitProducer.sendEmail(emailMessage);
    return ApiResponse.builder()
        .success(true)
        .message("Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản trước khi đăng nhập.")
        .build();
  }

  @Override
  public ApiResponse<?> authProfile() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = (User) authentication.getPrincipal();
    return ApiResponse.builder().success(true).data(UserMapper.toUserResponse(user)).build();
  }

  @Override
  public ApiResponse<?> changePassword(ChangePasswordRequest changePasswordRequest) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = (User) authentication.getPrincipal();
    if(!this.passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
      throw new UnauthorizedException("Password không chính xác.");
    }
    user.setPassword(this.passwordEncoder.encode(changePasswordRequest.getNewPassword()));
    this.userRepository.save(user);
    return ApiResponse.builder().success(true).message("Change password success").build();
  }

  @Override
  public void forgetPassword(String email) {
    if (!this.userRepository.existsByEmail(email)) {
      throw new ResourceNotFoundException("Không tồn tại tài khoản với email là: " + email);
    }
    String code = GenCode.generateRandomNumber(6);
    MailDetails mailDetails = MailDetails.builder()
        .to(email)
        .token(code)
        .build();
    EmailMessage emailMessage = new EmailMessage();
    emailMessage.setMessageType(EmailType.CODE_FORGET_PASSWORD);
    emailMessage.setMailDetails(mailDetails);
    this.rabbitProducer.sendEmail(emailMessage);
    this.redisService.saveKeyAndValue("resetPassword:email:"+email , code , "3" , TimeUnit.MINUTES );
  }

  @Override
  public void verifyResetPassword(String code, String email) {
    User user = this.userRepository.findByEmail(email);
    if (user == null) {
      throw new BadRequestException("Không tồn tại người dùng có email là: " + email);
    }
    String codeCheck = this.redisService.getValue("resetPassword:email:" + email, new TypeReference<String>() {});
    if (codeCheck == null || !codeCheck.equals(code)) {
      this.redisService.deleteKey("resetPassword:email:" + email);
      throw new BadRequestException("mã xác thực reset mật khẩu đã hết hạn hoặc là không đúng vui lòng thực hiện lại reset mật khẩu.");
    }

    String newPassword = GenPassword.generateSecurePassword();
    MailDetails mailDetails = MailDetails.builder()
        .to(email)
        .rawPassword(newPassword)
        .build();

    EmailMessage emailMessage = new EmailMessage();
    emailMessage.setMessageType(EmailType.RESET_PASSWORD);
    emailMessage.setMailDetails(mailDetails);
    this.rabbitProducer.sendEmail(emailMessage);
    user.setPassword(this.passwordEncoder.encode(newPassword));
    this.userRepository.save(user);
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
  public ApiResponse<?> refresh(RefreshTokenReqest refreshTokenReqest, HttpServletRequest req) {
    Long userId = refreshTokenReqest.getUserId();
    String refreshToken = this.cookieUtils.getCookieValue(req, "refresh_token");
    String refreshRedis = this.redisService.getValue("auth:refreshToken:"+userId,new TypeReference<String>() {});
    if(refreshRedis == null) throw new UnauthorizedException("Refresh token expired");
    else if(!refreshRedis.equals(refreshToken)) {
      throw new UnauthorizedException("Refresh token invalid");
    }
    User user = this.userRepository.findById(userId).orElseThrow(() -> new BadRequestException(
        "User not found"));
    String accessTokenNew = this.jwtUtils.JwtGenerator(user, secretKey , expirationTimeAccess);
    this.redisService.saveKeyAndValue("auth:accessToken:"+user.getId() , accessTokenNew , expirationTimeAccess , TimeUnit.SECONDS);
    return ApiResponse.builder().success(true).message("access_token new").data(Map.of("access_token" , accessTokenNew)).build();
  }

  @Override
  public ApiResponse<?> logout(HttpServletResponse res) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = (User) authentication.getPrincipal();
    String accessToken =  this.redisService.getValue("auth:accessToken:"+user.getId(),new TypeReference<String>() {});
    this.redisService.saveKeyAndValue("BlackList:"+accessToken+user.getId() , accessToken , expirationTimeAccess , TimeUnit.SECONDS);
    this.redisService.deleteKey("auth:accessToken:"+user.getId());
    this.redisService.deleteKey("auth:refreshToken:"+user.getId());
    ResponseCookie cookie = ResponseCookie.from("refresh_token","")
        .sameSite("None")
        .maxAge(0)
        .secure(true)
        .httpOnly(true)
        .path("/")
        .build();
    res.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    return ApiResponse.builder().success(true).message("logout success").build();
  }

}
