package com.bestzedcoder.project3.booking_tour_hotel.service.iml;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.UserUpdatingProfile;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.exception.BadRequestException;
import com.bestzedcoder.project3.booking_tour_hotel.exception.ResourceNotFoundException;
import com.bestzedcoder.project3.booking_tour_hotel.mapper.UserMapper;
import com.bestzedcoder.project3.booking_tour_hotel.upload.ICloudinaryService;
import com.bestzedcoder.project3.booking_tour_hotel.model.ImageProfile;
import com.bestzedcoder.project3.booking_tour_hotel.model.Profile;
import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import com.bestzedcoder.project3.booking_tour_hotel.repository.ProfileRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.UserRepository;
import com.bestzedcoder.project3.booking_tour_hotel.service.IProfileService;
import jakarta.transaction.Transactional;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProfileService implements IProfileService {
  private final ProfileRepository profileRepository;
  private final UserRepository userRepository;
  private final ICloudinaryService cloudinaryService;

  @Override
  @Transactional
  public ApiResponse<?> update(UserUpdatingProfile userUpdatingProfile,
      MultipartFile multipartFile) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Profile profile = user.getProfile();
    profile.setFullName(userUpdatingProfile.getFullName());
    profile.setPhoneNumber(userUpdatingProfile.getPhone());
    profile.setAddress(userUpdatingProfile.getAddress());
    if (multipartFile != null) {
      if (profile.getImage() != null) {
        this.cloudinaryService.deleteImage(profile.getImage().getPublicId());
      }
      Map<String, String> result = this.cloudinaryService.validationAndUpload(multipartFile,
          "profile");
      ImageProfile image = profile.getImage() != null ? profile.getImage() : new ImageProfile();
      image.setPublicId(result.get("public_id"));
      image.setUrl(result.get("url"));
      profile.setImage(image);
    }
    user.setUpdateProfile(true);
    user.setProfile(profile);
    profile.setUser(user);
    this.userRepository.save(user);
    return ApiResponse.builder().success(true).message("Update profile success").data(UserMapper.toUserResponse(user)).build();
  }
}
