package com.bestzedcoder.project3.booking_tour_hotel.mapper;

import com.bestzedcoder.project3.booking_tour_hotel.dto.response.GetUserAllResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.UserResponse;
import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
  public static UserResponse toUserResponse(User user) {
    UserResponse userResponse = new UserResponse();
    userResponse.setId(user.getId());
    userResponse.setUsername(user.getUsername());
    userResponse.setEmail(user.getEmail());
    userResponse.setPhone(user.getProfile().getPhoneNumber());
    userResponse.setAddress(user.getProfile().getAddress());
    userResponse.setFullName(user.getProfile().getFullName());
    userResponse.setRoles(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new));
    userResponse.setActive(user.isEnabled());
    userResponse.setUpdateProfile(user.getUpdateProfile());
    if (user.getProfile().getImage() != null) {
      userResponse.setUrlImage(user.getProfile().getImage().getUrl());
    }
    return userResponse;
  }

  public static GetUserAllResponse toGetUserAllResponse(User user) {
    GetUserAllResponse userResponse = new GetUserAllResponse();
    userResponse.setId(user.getId());
    userResponse.setUsername(user.getUsername());
    userResponse.setEmail(user.getEmail());
    userResponse.setFullName(user.getProfile().getFullName());
    userResponse.setRoles(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new));
    userResponse.setActive(user.isEnabled());
    return userResponse;
  }
}
