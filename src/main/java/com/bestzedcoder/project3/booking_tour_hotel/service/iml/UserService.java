package com.bestzedcoder.project3.booking_tour_hotel.service.iml;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserUpdatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.GetUserAllResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.PageResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.UserResponse;
import com.bestzedcoder.project3.booking_tour_hotel.exception.BadRequestException;
import com.bestzedcoder.project3.booking_tour_hotel.exception.ResourceNotFoundException;
import com.bestzedcoder.project3.booking_tour_hotel.mail.IEmailService;
import com.bestzedcoder.project3.booking_tour_hotel.mail.MailDetails;
import com.bestzedcoder.project3.booking_tour_hotel.mapper.UserMapper;
import com.bestzedcoder.project3.booking_tour_hotel.model.ImageProfile;
import com.bestzedcoder.project3.booking_tour_hotel.model.Profile;
import com.bestzedcoder.project3.booking_tour_hotel.model.Role;
import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import com.bestzedcoder.project3.booking_tour_hotel.redis.IRedisService;
import com.bestzedcoder.project3.booking_tour_hotel.repository.RoleRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.UserRepository;
import com.bestzedcoder.project3.booking_tour_hotel.security.GenPassword;
import com.bestzedcoder.project3.booking_tour_hotel.service.IUserService;
import com.bestzedcoder.project3.booking_tour_hotel.upload.ICloudinaryService;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class UserService implements IUserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final RoleRepository roleRepository;
  private final ICloudinaryService cloudinaryService;
  private final IRedisService redisService;
  private final IEmailService emailService;

  @Override
  @Transactional
  public ApiResponse<?> create(UserCreatingRequest request)
      throws BadRequestException {
    String email = request.getEmail();
    String username = request.getUsername();
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new BadRequestException("Username đã tồn tại");
    }
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new BadRequestException("Email đã tồn tại");
    }

    String rawPassword = GenPassword.generateSecurePassword();
    String hashedPassword = passwordEncoder.encode(rawPassword);
    String phone = request.getPhone();
    String fullName = request.getFullName();
    String address = request.getAddress();
    Set<Role> roles = Arrays.stream(request.getRoles())
        .map(roleName -> {
          try {
            return this.roleRepository.findByName(roleName)
                .orElseThrow(() -> new BadRequestException("Role không hợp lệ: " + roleName));
          } catch (BadRequestException e) {
            throw new RuntimeException(e);
          }
        })
        .collect(Collectors.toSet());
    User user = new User();
    user.setUsername(username);
    user.setEmail(email);
    user.setPassword(hashedPassword);
    user.setEnabled(true);
    user.setUpdateProfile(true);
    user.setRoles(roles);
    Profile profile = new Profile();
    profile.setFullName(fullName);
    profile.setAddress(address);
    profile.setPhoneNumber(phone);
    profile.setUser(user);
    user.setProfile(profile);
    this.userRepository.save(user);
    MailDetails emailDetails = MailDetails.builder()
        .to(email)
        .rawPassword(rawPassword)
        .fullName(fullName)
        .username(username)
        .build();
    this.emailService.sendInfoUserDetails(emailDetails);
    return ApiResponse.builder().success(true).message("Created user successfully").build();

  }

  @Override
  public ApiResponse<?> getUserById(Long id) {
    User user = this.userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
        "User not found with id: " + id));
    return ApiResponse.builder().success(true).data(UserMapper.toUserResponse(user)).message("success").build();
  }

  @Override
  @Transactional
  public ApiResponse<?> updateUserById(Long id, UserUpdatingRequest request, MultipartFile image) {
    User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
        "Không tìm được user với id là: " + id));
    user.setEnabled(request.isActive());
    Set<Role> roles = Arrays.stream(request.getRoles())
        .map(roleName -> roleRepository.findByName(roleName)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName)))
        .collect(Collectors.toSet());
    user.setRoles(roles);
    Profile profile = user.getProfile();
    profile.setFullName(request.getFullName());
    profile.setAddress(request.getAddress());
    profile.setPhoneNumber(request.getPhone());
    if(image != null) {
      if (profile.getImage() != null) {
        this.cloudinaryService.deleteImage(profile.getImage().getPublicId());
      }
      Map<String,String> res = this.cloudinaryService.validationAndUpload(image ,"profile");
      ImageProfile img = profile.getImage() != null ? profile.getImage() : new ImageProfile();
      img.setPublicId(res.get("public_id"));
      img.setUrl(res.get("url"));
      profile.setImage(img);
    }
    this.userRepository.save(user);
    String accessToken =  this.redisService.getValue("auth:accessToken:"+user.getId(),new TypeReference<String>() {});
    if(accessToken != null) {
      this.redisService.saveKeyAndValue("BlackList:"+accessToken+user.getId() , accessToken , "3" , TimeUnit.MINUTES);
      this.redisService.deleteKey("auth:accessToken:"+user.getId());
      this.redisService.deleteKey("auth:refreshToken:"+user.getId());
    }
    return ApiResponse.builder().success(true).message("Updated user successfully").build();
  }

  @Override
  public PageResponse<?> getAllUsers(int page,int limit) {
    String key = String.format("search:users:page:%d:limit:%d", page, limit);
    PageResponse<UserResponse> dataCache = this.redisService.getValue(key, new TypeReference<PageResponse<UserResponse>>() {});
    if(dataCache != null) {
      dataCache.setSuccess(Boolean.TRUE);
      dataCache.setMessage("Get all users successfully");
      return dataCache;

    }

    Pageable pageable = PageRequest.of(page - 1, limit);
    Page<User> data = this.userRepository.findAll(pageable);
    List<GetUserAllResponse> users = data.getContent().stream().map(UserMapper::toGetUserAllResponse).toList();
    PageResponse<GetUserAllResponse> response = PageResponse.<GetUserAllResponse>builder()
        .currentPages(page)
        .pageSizes(limit)
        .totalPages(data.getTotalPages())
        .totalElements(data.getTotalElements())
        .result(users)
        .build();
    this.redisService.saveKeyAndValue(key,response,"2" , TimeUnit.MINUTES);
    response.setSuccess(Boolean.TRUE);
    response.setMessage("Get all users successfully");
    return response;
  }

  @Override
  @Transactional
  public ApiResponse<?> deleteUserById(Long id) {
    this.userRepository.deleteById(id);
    return ApiResponse.builder().success(true).message("Delete user success").build();
  }
}
