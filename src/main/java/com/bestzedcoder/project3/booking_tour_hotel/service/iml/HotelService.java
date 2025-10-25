package com.bestzedcoder.project3.booking_tour_hotel.service.iml;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.HotelCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.mapper.HotelMapper;
import com.bestzedcoder.project3.booking_tour_hotel.model.Hotel;
import com.bestzedcoder.project3.booking_tour_hotel.model.ImageHotel;
import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import com.bestzedcoder.project3.booking_tour_hotel.repository.HotelRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.UserRepository;
import com.bestzedcoder.project3.booking_tour_hotel.service.IHotelService;
import com.bestzedcoder.project3.booking_tour_hotel.upload.ICloudinaryService;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class HotelService implements IHotelService {
  private final ICloudinaryService cloudinaryService;
  private final UserRepository userRepository;
  private final HotelRepository hotelRepository;
  @Override
  public ApiResponse<?> create(HotelCreatingRequest hotelCreatingRequest, MultipartFile[] images) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User owner = (User) authentication.getPrincipal();
    Set<ImageHotel> imageHotels = new HashSet<>();
    Hotel hotel = new Hotel();
    if(images != null) {
      for (MultipartFile image: images) {
        Map<String, String> resUploadImage = this.cloudinaryService.validationAndUpload(image , "hotel");
        ImageHotel imageHotel = ImageHotel.builder().url(resUploadImage.get("url")).publicId(resUploadImage.get("public_id")).hotel(hotel).build();
        imageHotels.add(imageHotel);
      }
    }
    hotel.setOwner(owner);
    hotel.setImages(imageHotels);
    hotel.setHotel_name(hotelCreatingRequest.getHotelName());
    hotel.setHotel_address(hotelCreatingRequest.getHotelAddress());
    hotel.setHotel_city(hotelCreatingRequest.getHotelCity());
    hotel.setHotel_description(hotelCreatingRequest.getHotelDescription());
    hotel.setHotel_star(hotelCreatingRequest.getHotelStar());
    owner.setHotels(List.of(hotel));
    this.userRepository.save(owner);
    return ApiResponse.builder().success(true).message("Created hotel successfully.").build();
  }

  @Override
  public ApiResponse<?> getHotelsByOwnerId(Long ownerId) {
    List<Hotel> hotels = this.hotelRepository.findByOwnerId(ownerId);
    if(hotels.isEmpty()) {
      return ApiResponse.builder().success(true).message("Không tìm được khách sạn sở hữu bởi user có id là: " + ownerId).build();
    }
    return ApiResponse.builder().success(true).message("Success").data(hotels.stream().map(
        HotelMapper::hotelToHotelResponse).toList()).build();
  }
}
