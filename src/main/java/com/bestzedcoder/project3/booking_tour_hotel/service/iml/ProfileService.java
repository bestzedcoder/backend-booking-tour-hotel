package com.bestzedcoder.project3.booking_tour_hotel.service.iml;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserUpdatingProfile;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.exception.BadRequestException;
import com.bestzedcoder.project3.booking_tour_hotel.model.Profile;
import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import com.bestzedcoder.project3.booking_tour_hotel.repository.ProfileRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.UserRepository;
import com.bestzedcoder.project3.booking_tour_hotel.service.IProfileService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService implements IProfileService {
  private final ProfileRepository profileRepository;
  private final UserRepository userRepository;

  @Override
  public ApiResponse<?> update(Long id, UserUpdatingProfile userUpdatingProfile) {
    Profile profile = this.profileRepository.findProfileByUserId(id).orElseThrow(() -> {throw new BadRequestException("Profile not found");
    });
    profile.setFullName(userUpdatingProfile.getFullName());
    profile.setPhoneNumber(userUpdatingProfile.getPhoneNumber());
    profile.setAddress(userUpdatingProfile.getAddress());
    User user = this.userRepository.findById(id).orElseThrow(() -> {throw new BadRequestException("User not found");});
    user.setUpdateProfile(true);
    user.setProfile(profile);
    profile.setUser(user);
    this.userRepository.save(user);
    return ApiResponse.builder().success(true).message("success").data(Map.of("updateProfile", true,"profile",profile)).build();
  }
}
