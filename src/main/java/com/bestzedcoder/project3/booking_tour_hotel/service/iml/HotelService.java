package com.bestzedcoder.project3.booking_tour_hotel.service.iml;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.HotelCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.RoomsCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.HotelResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.HotelSearchResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.PageResponse;
import com.bestzedcoder.project3.booking_tour_hotel.enums.HotelStar;
import com.bestzedcoder.project3.booking_tour_hotel.enums.RoomStatus;
import com.bestzedcoder.project3.booking_tour_hotel.enums.RoomType;
import com.bestzedcoder.project3.booking_tour_hotel.exception.BadRequestException;
import com.bestzedcoder.project3.booking_tour_hotel.mapper.HotelMapper;
import com.bestzedcoder.project3.booking_tour_hotel.model.Hotel;
import com.bestzedcoder.project3.booking_tour_hotel.model.ImageHotel;
import com.bestzedcoder.project3.booking_tour_hotel.model.Room;
import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import com.bestzedcoder.project3.booking_tour_hotel.redis.IRedisService;
import com.bestzedcoder.project3.booking_tour_hotel.repository.HotelRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.UserRepository;
import com.bestzedcoder.project3.booking_tour_hotel.service.IHotelService;
import com.bestzedcoder.project3.booking_tour_hotel.upload.ICloudinaryService;
import com.fasterxml.jackson.core.type.TypeReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
  private final IRedisService redisService;
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
    owner.getHotels().add(hotel);
    this.userRepository.save(owner);
    return ApiResponse.builder().success(true).message("Created hotel successfully.").build();
  }

  @Override
  public ApiResponse<?> getHotelsByOwnerId(Long ownerId) {
    String cacheKey = "getHotelsByOwner:" + ownerId;

    List<HotelResponse> cachedHotels = redisService.getValue(cacheKey , new TypeReference<List<HotelResponse>>(){});
    if (cachedHotels != null && !cachedHotels.isEmpty()) {
      return ApiResponse.builder()
          .success(true)
          .message("Lấy danh sách khách sạn từ cache")
          .data(cachedHotels)
          .build();
    }

    List<Hotel> hotels = hotelRepository.findByOwnerId(ownerId);
    if (hotels.isEmpty()) {
      return ApiResponse.builder()
          .success(true)
          .message("Không tìm được khách sạn sở hữu bởi user có id = " + ownerId)
          .build();
    }
    List<HotelResponse> responses = hotels.stream()
        .map(HotelMapper::hotelToHotelResponse)
        .toList();

    redisService.saveKeyAndValue(cacheKey, responses, "10", TimeUnit.MINUTES);

    return ApiResponse.builder()
        .success(true)
        .message("Lấy danh sách khách sạn thành công")
        .data(responses)
        .build();
  }

  @Override
  public ApiResponse<?> createRoom(Long hotelId, RoomsCreatingRequest roomsCreatingRequest) {
    Hotel hotel = this.hotelRepository.findById(hotelId).orElseThrow(() -> new BadRequestException(
        "Không tồn tại hotel với id: " + hotelId));
    int count = roomsCreatingRequest.getQuantity();
    RoomType roomType = roomsCreatingRequest.getRoomType();
    int countTypeRoom = hotel.getRooms().stream().map(room -> room.getType() == roomType).toList().size();
    List<Room> rooms = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      int roomId = countTypeRoom + i;
      Room room = Room.builder().roomName(roomType+"_"+roomId).type(roomType).pricePerDay(
          roomsCreatingRequest.getPricePerDay()).pricePerHour(roomsCreatingRequest.getPricePerHour()).status(
          RoomStatus.AVAILABLE).hotel(hotel).build();
      rooms.add(room);
    }
    hotel.getRooms().addAll(rooms);
    this.hotelRepository.save(hotel);
    return ApiResponse.builder().success(true).message("Created success").build();
  }

  @Override
  public PageResponse<?> searchByUser(int page, int limit, String hotelName, String address,
      String city, HotelStar hotelStar) {
    String key = String.format("search:hotel:p%d:l%d:n%s:a%s:c%s:s%s",
        page, limit,
        hotelName != null ? hotelName.toLowerCase() : "",
        address != null ? address.toLowerCase() : "",
        city != null ? city.toLowerCase() : "",
        hotelStar != null ? hotelStar.toString().toLowerCase() : "");
    PageResponse<HotelSearchResponse> dataCache = this.redisService.getValue(key,
        new TypeReference<PageResponse<HotelSearchResponse>>() {});
    if(dataCache != null) {
      return dataCache;
    }
    Pageable pageable = PageRequest.of(page - 1, limit);
    Page<Hotel> data = this.hotelRepository.searchHotels(hotelName,address,city,hotelStar,pageable);
    List<HotelSearchResponse> hotels = data.getContent()
        .stream()
        .map(HotelMapper::hotelToHotelSearchResponse)
        .collect(Collectors.toList());

    PageResponse<HotelSearchResponse> response = PageResponse.<HotelSearchResponse>builder()
        .result(hotels)
        .currentPages(data.getNumber() + 1)
        .pageSizes(data.getSize())
        .totalPages(data.getTotalPages())
        .totalElements(data.getTotalElements())
        .build();
    this.redisService.saveKeyAndValue(key, response, "1", TimeUnit.MINUTES);
    return response;
  }
}
