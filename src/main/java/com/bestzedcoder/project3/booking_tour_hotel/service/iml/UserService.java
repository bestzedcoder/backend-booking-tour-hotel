package com.bestzedcoder.project3.booking_tour_hotel.service.iml;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserUpdatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.enums.ErrorCode;
import com.bestzedcoder.project3.booking_tour_hotel.exception.BadRequestException;
import com.bestzedcoder.project3.booking_tour_hotel.mapper.UserMapper;
import com.bestzedcoder.project3.booking_tour_hotel.model.Profile;
import com.bestzedcoder.project3.booking_tour_hotel.model.Role;
import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import com.bestzedcoder.project3.booking_tour_hotel.repository.ProfileRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.RoleRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.UserRepository;
import com.bestzedcoder.project3.booking_tour_hotel.service.IUserService;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService implements IUserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final RoleRepository roleRepository;
  private final ProfileRepository profileRepository;

  @Override
  public ApiResponse<?> create(UserCreatingRequest request)
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
    Role role = this.roleRepository.findByName("ROLE_CUSTOMER");
    Profile profile = Profile.builder().fullName(request.getFullName() == null ? "anonymous" : request.getFullName()).address(
        request.getAddress()).phoneNumber(request.getPhone()).build();
    User newUser = User.builder()
        .username(username)
        .password(hashedPassword)
        .email(email)
        .enabled(true)
        .updateProfile(true)
        .profile(profile)
        .roles(Set.of(role))
        .build();
    profile.setUser(newUser);
    this.userRepository.save(newUser);
    return ApiResponse.builder()
        .success(true)
        .message("User created")
        .data(UserMapper.toUserResponse(newUser)).build();
  }

  @Override
  public ApiResponse<?> getUserById(Long id) throws BadRequestException {
    User user = this.userRepository.findById(id).orElseThrow(() -> {
      throw new BadRequestException("User not found with id: " + id);
    });
    return ApiResponse.builder().success(true).data(UserMapper.toUserResponse(user)).message("success").build();
  }

  @Override
  public ApiResponse<?> updateUserById(Long id, UserUpdatingRequest request)
      throws BadRequestException {
    User user = this.userRepository.findById(id).orElseThrow(() -> {throw new BadRequestException("User not found with id: " + id);});
    String fullName = request.getFullName();
    String phone = request.getPhone();
    String []roles = request.getRoles();
    if (roles.length == 0) {
      roles = new String[]{"ROLE_CUSTOMER"};
    }
    HashSet<Role> roleSet = new HashSet<>();
    for (String role : roles) {
      Role roleEnum = this.roleRepository.findByName(role);
      if (roleEnum == null) {
        throw new BadRequestException(ErrorCode.ROLE_NOT_EXISTED.getMessage());
      }
      roleSet.add(roleEnum);
    }
    Profile profile = this.profileRepository.findProfileByUserId(user.getId()).orElseThrow(() -> {
      throw new BadRequestException("Profile not found with user id: " + user.getId());
    });
    profile.setFullName(fullName);
    profile.setPhoneNumber(phone);
    profile.setAddress(request.getAddress());
    user.setRoles(roleSet);
    user.setProfile(profile);
    user.setUpdateProfile(true);
    profile.setUser(user);
    this.userRepository.save(user);
    return ApiResponse.builder().success(true).message("Updated user success.").build();
  }

  @Override
  public ApiResponse<?> getAllUsers() {
    var users = this.userRepository.findAll();
    return ApiResponse.builder().success(true).data(users.stream().map(UserMapper::toUserResponse).toList()).build();
  }
}
