package com.bestzedcoder.project3.booking_tour_hotel.service.iml;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.HotelCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.HotelUpdatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.RoomUpdatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.RoomsCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.HotelSearchResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.InfoHotelDetails;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.PageResponse;
import com.bestzedcoder.project3.booking_tour_hotel.enums.HotelStar;
import com.bestzedcoder.project3.booking_tour_hotel.enums.RoomStatus;
import com.bestzedcoder.project3.booking_tour_hotel.enums.RoomType;
import com.bestzedcoder.project3.booking_tour_hotel.exception.BadRequestException;
import com.bestzedcoder.project3.booking_tour_hotel.exception.ResourceNotFoundException;
import com.bestzedcoder.project3.booking_tour_hotel.mapper.HotelMapper;
import com.bestzedcoder.project3.booking_tour_hotel.model.Hotel;
import com.bestzedcoder.project3.booking_tour_hotel.model.ImageHotel;
import com.bestzedcoder.project3.booking_tour_hotel.model.Room;
import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import com.bestzedcoder.project3.booking_tour_hotel.redis.IRedisService;
import com.bestzedcoder.project3.booking_tour_hotel.repository.HotelRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.RoomRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.UserRepository;
import com.bestzedcoder.project3.booking_tour_hotel.service.IHotelService;
import com.bestzedcoder.project3.booking_tour_hotel.upload.ICloudinaryService;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
  private final RoomRepository roomRepository;
  @Override
  public ApiResponse<?> create(HotelCreatingRequest hotelCreatingRequest, MultipartFile[] images) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User owner = (User) authentication.getPrincipal();
    List<ImageHotel> imageHotels = new ArrayList<>();
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
    hotel.setHotelName(hotelCreatingRequest.getHotelName());
    hotel.setHotelAddress(hotelCreatingRequest.getHotelAddress());
    hotel.setHotelCity(hotelCreatingRequest.getHotelCity());
    hotel.setHotelDescription(hotelCreatingRequest.getHotelDescription());
    hotel.setHotelStar(hotelCreatingRequest.getHotelStar());
    owner.getHotels().add(hotel);
    this.userRepository.save(owner);
    return ApiResponse.builder().success(true).message("Created hotel successfully.").build();
  }

  @Override
  public PageResponse<?> getHotelsByOwner(int page, int limit, String hotelName, String city ,
      HotelStar hotelStar) {
    User owner = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long ownerId = owner.getId();
    String cacheKey = String.format("search:hotel:owner:%d:name:%s:city:%s:star:%s:page:%d:limit:%d",
        ownerId,
        hotelName != null ? hotelName : "",
        city != null ? city : "",
        hotelStar != null ? hotelStar.name() : "",
        page,
        limit
    );

    PageResponse<HotelSearchResponse> cachedHotels = redisService.getValue(cacheKey , new TypeReference<PageResponse<HotelSearchResponse>>(){});
    if (cachedHotels != null) {
      return cachedHotels;
    }

    Pageable pageable = PageRequest.of(page - 1, limit);

    Page<Hotel> hotels = this.hotelRepository.searchHotelsByOwnerId(ownerId,hotelName,city,hotelStar,pageable);
    List<HotelSearchResponse> data = hotels.getContent().stream()
        .map(HotelMapper::hotelToHotelSearchResponse)
        .toList();
    PageResponse<HotelSearchResponse> response = PageResponse.<HotelSearchResponse>builder().currentPages(page).result(data).success(true).message("Search successfully").pageSizes(limit).totalPages(hotels.getTotalPages()).totalElements(hotels.getTotalElements()).build();
    redisService.saveKeyAndValue(cacheKey, response, "2", TimeUnit.MINUTES);

    return response;
  }

  @Override
  public ApiResponse<?> createRoom(Long hotelId, RoomsCreatingRequest roomsCreatingRequest) {
    Hotel hotel = this.hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException(
        "Không tồn tại hotel với id: " + hotelId));
    int count = roomsCreatingRequest.getQuantity();
    RoomType roomType = roomsCreatingRequest.getRoomType();
    int countTypeRoom = (int) hotel.getRooms().stream().filter(room -> room.getType() == roomType).count();
    List<Room> rooms = new ArrayList<>();
    for (int i = 1; i <= count; i++) {
      int roomId = countTypeRoom + i;
      Room room = Room.builder().roomName(roomType.toString().substring(0,2)+"_"+roomId).type(roomType).pricePerDay(
          roomsCreatingRequest.getPricePerDay()).pricePerHour(roomsCreatingRequest.getPricePerHour()).status(
          RoomStatus.AVAILABLE).hotel(hotel).build();
      rooms.add(room);
    }
    hotel.getRooms().addAll(rooms);
    this.hotelRepository.save(hotel);
    return ApiResponse.builder().success(true).message("Created success").build();
  }

  @Override
  public PageResponse<?> searchByUser(int page, int limit, String hotelName,
      String city, HotelStar hotelStar) {
    String key = String.format("search:hotel:page:%d:limit:%d:name:%s:city:%s:star%s",
        page, limit,
        hotelName != null ? hotelName.toLowerCase() : "",
        city != null ? city.toLowerCase() : "",
        hotelStar != null ? hotelStar.toString().toLowerCase() : "");
    PageResponse<HotelSearchResponse> dataCache = this.redisService.getValue(key,
        new TypeReference<PageResponse<HotelSearchResponse>>() {});
    if(dataCache != null) {
      dataCache.setSuccess(true);
      dataCache.setMessage("Search successfully");
      return dataCache;
    }
    Pageable pageable = PageRequest.of(page - 1, limit);
    Page<Hotel> data = this.hotelRepository.searchHotels(hotelName,city,hotelStar,pageable);
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
    response.setSuccess(true);
    response.setMessage("Search successfully");
    return response;
  }

  @Override
  public PageResponse<?> searchByAdmin(int page, int limit, String hotelName, String city,
      HotelStar hotelStar, String owner) {
    User user = this.userRepository.findByUsername(owner);
    if (user == null) {
      throw new ResourceNotFoundException("Owner not found");
    }
    Long ownerId = user.getId();
    String cacheKey = String.format("search:hotel:owner:%d:name:%s:city:%s:star:%s:page:%d:limit:%d",
        ownerId,
        hotelName != null ? hotelName : "",
        city != null ? city : "",
        hotelStar != null ? hotelStar.name() : "",
        page,
        limit
    );

    PageResponse<HotelSearchResponse> cachedHotels = redisService.getValue(cacheKey , new TypeReference<PageResponse<HotelSearchResponse>>(){});
    if (cachedHotels != null) {
      return cachedHotels;
    }

    Pageable pageable = PageRequest.of(page - 1, limit);

    Page<Hotel> hotels = this.hotelRepository.searchHotelsByOwnerId(ownerId,hotelName,city,hotelStar,pageable);
    List<HotelSearchResponse> data = hotels.getContent().stream()
        .map(HotelMapper::hotelToHotelSearchResponse)
        .toList();
    PageResponse<HotelSearchResponse> response = PageResponse.<HotelSearchResponse>builder().currentPages(page).result(data).success(true).message("Search successfully").pageSizes(limit).totalPages(hotels.getTotalPages()).totalElements(hotels.getTotalElements()).build();
    redisService.saveKeyAndValue(cacheKey, response, "2", TimeUnit.MINUTES);

    return response;
  }

  @Override
  public ApiResponse<?> getHotelById(Long hotelId) {
    Hotel hotel = this.hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
    User owner = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (!owner.getId().equals(hotel.getOwner().getId())) {
      throw new BadRequestException("Error: You are not the owner of this hotel");
    }
    return ApiResponse.builder().success(true).data(HotelMapper.hotelToHotelResponse(hotel)).message("Hotel found").build();
  }

  @Override
  public ApiResponse<?> deleteHotel(Long hotelId) {
    Hotel hotel = this.hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
    for (ImageHotel imageHotel : hotel.getImages()) {
      this.cloudinaryService.deleteImage(imageHotel.getPublicId());
    }
    User owner = hotel.getOwner();
    owner.getHotels().remove(hotel);
    this.userRepository.save(owner);
    return ApiResponse.builder().success(true).message("Deleted success").build();
  }

  @Transactional
  @Override
  public ApiResponse<?> updateRoom(Long hotelId, Long roomId, RoomUpdatingRequest request) {
    if (!hotelRepository.existsById(hotelId)) {
      throw new ResourceNotFoundException("Hotel not found");
    }

    int updated = this.roomRepository.updateRoomByHotelId(
        hotelId,
        roomId,
        request.getPricePerDay(),
        request.getPricePerHour(),
        request.getRoomStatus()
    );

    if (updated == 0) {
      throw new ResourceNotFoundException("Room not found for this hotel");
    }

    return ApiResponse.builder()
        .success(true)
        .message("Updated room successfully.")
        .build();
  }

  @Override
  public ApiResponse<?> updateHotel(Long hotelId, HotelUpdatingRequest hotelUpdatingRequest,
      MultipartFile[] images) {
    Hotel hotel = this.hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
    User owner = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (!owner.getId().equals(hotel.getOwner().getId())) {
      throw new BadRequestException("Error: You are not the owner of this hotel");
    }
    List<ImageHotel> imageHotels = hotel.getImages();
    if (hotelUpdatingRequest.getImagesOld().length < imageHotels.size()) {
      List<ImageHotel> imagesDelete = imageHotels.stream()
          .filter(img -> Arrays.stream(hotelUpdatingRequest.getImagesOld())
              .noneMatch(url -> url.equals(img.getUrl())))
          .toList();
      for (ImageHotel imageHotel : imagesDelete) {
        this.cloudinaryService.deleteImage(imageHotel.getPublicId());
        hotel.getImages().remove(imageHotel);
      }
    }

    if (images != null) {
      for(MultipartFile file : images) {
        Map<String , String> res = this.cloudinaryService.validationAndUpload(file,"hotel");
        ImageHotel imageHotel = ImageHotel.builder().url(res.get("url")).publicId(res.get("public_id")).hotel(hotel).build();
        hotel.getImages().add(imageHotel);
      }
    }

    hotel.setHotelDescription(hotelUpdatingRequest.getHotelDescription());
    hotel.setHotelAddress(hotelUpdatingRequest.getHotelAddress());
    hotel.setHotelCity(hotelUpdatingRequest.getHotelCity());
    hotel.setHotelName(hotelUpdatingRequest.getHotelName());
    hotel.setHotelStar(hotelUpdatingRequest.getHotelStar());

    this.hotelRepository.save(hotel);

    return ApiResponse.builder().success(true).message("Hotel updated successfully").build();
  }

  @Override
  public ApiResponse<?> infoHotelDetails(Long hotelId) {
    String key = String.format("search:hotel:info:%s", hotelId);
    ApiResponse<InfoHotelDetails> cacheData = this.redisService.getValue(key, new TypeReference<ApiResponse<InfoHotelDetails>>() {});
    if(cacheData != null) {
      return cacheData;
    }
    Hotel hotel = this.hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
    ApiResponse<InfoHotelDetails> response = ApiResponse.<InfoHotelDetails>builder()
        .success(true)
        .data(HotelMapper.hotelToHotelDetails(hotel))
        .message("Successfully get hotel details")
        .build();
    this.redisService.saveKeyAndValue(key , response , "2" , TimeUnit.MINUTES);
    return response;
  }

  @Override
  public ApiResponse<?> infoBooking(Long hotelId, Long roomId) {
    Hotel hotel = this.hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
    Room room = this.roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    return ApiResponse.builder().success(true).message("Info booking").data(HotelMapper.hotelToHotelAndRoomDetailsBooking(hotel,room)).build();
  }
}
