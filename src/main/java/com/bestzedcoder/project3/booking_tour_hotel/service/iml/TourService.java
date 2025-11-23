package com.bestzedcoder.project3.booking_tour_hotel.service.iml;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.TourCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.TourScheduleUpdatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.TourSearchByAdminParams;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.TourSearchParams;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.TourUpdatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.InfoTourDetails;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.PageResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.TourResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.TourSearchResponse;
import com.bestzedcoder.project3.booking_tour_hotel.exception.BadRequestException;
import com.bestzedcoder.project3.booking_tour_hotel.exception.ResourceNotFoundException;
import com.bestzedcoder.project3.booking_tour_hotel.mapper.TourMapper;
import com.bestzedcoder.project3.booking_tour_hotel.model.ImageTour;
import com.bestzedcoder.project3.booking_tour_hotel.model.Tour;
import com.bestzedcoder.project3.booking_tour_hotel.model.TourSchedule;
import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import com.bestzedcoder.project3.booking_tour_hotel.redis.IRedisService;
import com.bestzedcoder.project3.booking_tour_hotel.repository.TourRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.UserRepository;
import com.bestzedcoder.project3.booking_tour_hotel.service.ITourService;
import com.bestzedcoder.project3.booking_tour_hotel.upload.ICloudinaryService;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.transaction.Transactional;
import java.util.*;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class TourService implements ITourService {
  private final TourRepository tourRepository;
  private final IRedisService redisService;
  private final ICloudinaryService cloudinaryService;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public ApiResponse<?> createTour(TourCreatingRequest tourCreatingRequest,
      MultipartFile[] images) {
    Tour newTour = new Tour();
    newTour.setName(tourCreatingRequest.getTourName());
    newTour.setDescription(tourCreatingRequest.getTourDescription());
    newTour.setDuration(tourCreatingRequest.getDuration());
    newTour.setMaxPeople(tourCreatingRequest.getMaxPeople());
    newTour.setStartDate(tourCreatingRequest.getStartDate());
    newTour.setEndDate(tourCreatingRequest.getEndDate());
    newTour.setPrice(tourCreatingRequest.getTourPrice());
    newTour.setCity(tourCreatingRequest.getTourCity());
    int days = tourCreatingRequest.getDuration();
    List<TourSchedule> schedules = new ArrayList<>();
    for (int day = 0; day < days ; day++) {
       TourSchedule tourSchedule = new TourSchedule();
       tourSchedule.setDayNumber(day + 1);
       tourSchedule.setTitle(tourCreatingRequest.getTourSchedule()[day].getTitle());
       tourSchedule.setDescription(tourCreatingRequest.getTourSchedule()[day].getDescription());
       tourSchedule.setTour(newTour);
       schedules.add(tourSchedule);
    }
    newTour.setSchedules(schedules);
    List<ImageTour> imageTours = new ArrayList<>();
    for (MultipartFile image : images) {
      ImageTour imageTour = new ImageTour();
      Map<String , String> res = this.cloudinaryService.validationAndUpload(image , "tour");
      imageTour.setUrl(res.get("url"));
      imageTour.setPublicId(res.get("public_id"));
      imageTour.setTour(newTour);
      imageTours.add(imageTour);
    }
    newTour.setImages(imageTours);
    User owner = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    newTour.setOwner(owner);
    owner.getTours().add(newTour);
    this.userRepository.save(owner);
    return ApiResponse.builder().success(true).message("Created successfully.").build();
  }

  @Override
  @Transactional
  public ApiResponse<?> updateTour(Long id, TourUpdatingRequest tourUpdatingRequest,
      MultipartFile[] imageNews) {
    Tour tour = this.tourRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tour not found"));
    User owner = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if(!owner.getId().equals(tour.getOwner().getId())) {
      throw new BadRequestException("Error: You are not the owner of this tour");
    }
    tour.setName(tourUpdatingRequest.getTourName());
    tour.setCity(tourUpdatingRequest.getTourCity());
    tour.setDescription(tourUpdatingRequest.getTourDescription());
    tour.setMaxPeople(tourUpdatingRequest.getMaxPeople());
    tour.setPrice(tourUpdatingRequest.getTourPrice());
    List<ImageTour> imageTours = tour.getImages();

    if (tourUpdatingRequest.getImageOlds().length < imageTours.size()) {
      List<ImageTour> imagesDelete = imageTours.stream()
          .filter(img -> Arrays.stream(tourUpdatingRequest.getImageOlds())
              .noneMatch(url -> url.equals(img.getUrl())))
          .toList();
      for (ImageTour imageTour : imagesDelete) {
        this.cloudinaryService.deleteImage(imageTour.getPublicId());
        tour.getImages().remove(imageTour);
      }
    }

    if (imageNews != null) {
      for(MultipartFile file : imageNews) {
        Map<String , String> res = this.cloudinaryService.validationAndUpload(file,"tour");
        ImageTour imageTour = ImageTour.builder().url(res.get("url")).publicId(res.get("public_id")).tour(tour).build();
        tour.getImages().add(imageTour);
      }
    }
    this.tourRepository.save(tour);
    this.redisService.deleteByPattern("search:tour:info:*");
    this.redisService.deleteByPattern("search:tour:details:*");
    return ApiResponse.builder().success(true).message("Updated successfully.").build();
  }

  @Override
  @Transactional
  public ApiResponse<?> updateTourSchedule(Long tourId, Long tourScheduledId,
      TourScheduleUpdatingRequest tourScheduleUpdatingRequest) {
    Tour tour = this.tourRepository.findById(tourId).orElseThrow(() -> new ResourceNotFoundException("Tour not found"));
    TourSchedule tourSchedule = tour.getSchedules().stream().filter(s -> s.getId().equals(tourScheduledId)).findFirst().orElseThrow(
        () -> new ResourceNotFoundException("Tour schedule not found")
    );
    tourSchedule.setDescription(tourScheduleUpdatingRequest.getDescription());
    tourSchedule.setTitle(tourScheduleUpdatingRequest.getTitle());
    this.tourRepository.save(tour);
    this.redisService.deleteByPattern("search:tour:info:*");
    this.redisService.deleteByPattern("search:tour:details:*");
    return ApiResponse.builder().success(true).message("Updated successfully.").build();
  }

  @Override
  public PageResponse<?> searchByUser(TourSearchParams p) {
    String keyCache = String.format("search:tour:page:%d:limit:%d:name:%s:city:%s:start:%s:end:%s:min:%s:max:%s",
        p.getPage(),
        p.getLimit(),
        p.getTourName() == null ? "" : p.getTourName(),
        p.getTourCity() == null ? "" : p.getTourCity(),
        p.getStartDate() == null ? "" : p.getStartDate(),
        p.getEndDate() == null ? "" : p.getEndDate(),
        p.getPriceMin() == null ? "" : p.getPriceMin(),
        p.getPriceMax() == null ? "" : p.getPriceMax());
    PageResponse<TourSearchResponse> dataCache = this.redisService.getValue(keyCache,
        new TypeReference<PageResponse<TourSearchResponse>>() {});
    if (dataCache != null) {
      return dataCache;
    }

    Specification<Tour> spec = Specification.where(null);

    if (p.getTourName() != null && !p.getTourName().isEmpty()) {
      spec = spec.and((root, query, cb) ->
          cb.like(cb.lower(root.get("name")), "%" + p.getTourName().toLowerCase() + "%"));
    }

    if (p.getTourCity() != null && !p.getTourCity().isEmpty()) {
      spec = spec.and((root, query, cb) ->
          cb.like(cb.lower(root.get("city")), "%" + p.getTourCity().toLowerCase() + "%"));
    }

    if (p.getPriceMin() != null) {
      spec = spec.and((root, query, cb) ->
          cb.greaterThanOrEqualTo(root.get("price"), p.getPriceMin()));
    }

    if (p.getPriceMax() != null) {
      spec = spec.and((root, query, cb) ->
          cb.lessThanOrEqualTo(root.get("price"), p.getPriceMax()));
    }

    if (p.getStartDate() != null) {
      spec = spec.and((root, query, cb) ->
          cb.greaterThanOrEqualTo(root.get("startDate"), p.getStartDate()));
    }

    if (p.getEndDate() != null) {
      spec = spec.and((root, query, cb) ->
          cb.lessThanOrEqualTo(root.get("endDate"), p.getEndDate()));
    }


    Pageable pageable = PageRequest.of(p.getPage() - 1, p.getLimit());

    Page<Tour> pageData = tourRepository.findAll(spec, pageable);

    PageResponse<TourSearchResponse> response = PageResponse.<TourSearchResponse>builder()
        .currentPages(p.getPage())
        .totalPages(pageData.getTotalPages())
        .totalElements(pageData.getTotalElements())
        .pageSizes(p.getLimit())
        .success(true)
        .message("Search successfully.")
        .result(pageData.getContent().stream().map(TourMapper::tourToTourSearchResponse).toList())
        .build();
    this.redisService.saveKeyAndValue(keyCache , response , "2" , TimeUnit.MINUTES);
    return response;
  }

  @Override
  public PageResponse<?> searchByOwner(TourSearchParams p) {
    User owner = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String keyCache = String.format("search:tour:owner:%d:page:%d:limit:%d:name:%s:city:%s:start:%s:end:%s:min:%s:max:%s",
        owner.getId(),
        p.getPage(),
        p.getLimit(),
        p.getTourName() == null ? "" : p.getTourName(),
        p.getTourCity() == null ? "" : p.getTourCity(),
        p.getStartDate() == null ? "" : p.getStartDate(),
        p.getEndDate() == null ? "" : p.getEndDate(),
        p.getPriceMin() == null ? "" : p.getPriceMin(),
        p.getPriceMax() == null ? "" : p.getPriceMax());
    PageResponse<TourSearchResponse> dataCache = this.redisService.getValue(keyCache,
        new TypeReference<PageResponse<TourSearchResponse>>() {});
    if (dataCache != null) {
      return dataCache;
    }

    Specification<Tour> spec = Specification.where(
        (root, query, cb) -> cb.equal(root.get("owner").get("id"), owner.getId())
    );

    if (p.getTourName() != null && !p.getTourName().isEmpty()) {
      spec = spec.and((root, query, cb) ->
          cb.like(cb.lower(root.get("name")), "%" + p.getTourName().toLowerCase() + "%"));
    }

    if (p.getTourCity() != null && !p.getTourCity().isEmpty()) {
      spec = spec.and((root, query, cb) ->
          cb.like(cb.lower(root.get("city")), "%" + p.getTourCity().toLowerCase() + "%"));
    }

    if (p.getPriceMin() != null) {
      spec = spec.and((root, query, cb) ->
          cb.greaterThanOrEqualTo(root.get("price"), p.getPriceMin()));
    }

    if (p.getPriceMax() != null) {
      spec = spec.and((root, query, cb) ->
          cb.lessThanOrEqualTo(root.get("price"), p.getPriceMax()));
    }

    if (p.getStartDate() != null) {
      spec = spec.and((root, query, cb) ->
          cb.greaterThanOrEqualTo(root.get("startDate"), p.getStartDate()));
    }

    if (p.getEndDate() != null) {
      spec = spec.and((root, query, cb) ->
          cb.lessThanOrEqualTo(root.get("endDate"), p.getEndDate()));
    }


    Pageable pageable = PageRequest.of(p.getPage() - 1, p.getLimit());

    Page<Tour> pageData = tourRepository.findAll(spec, pageable);

    PageResponse<TourSearchResponse> response = PageResponse.<TourSearchResponse>builder()
        .currentPages(p.getPage())
        .totalPages(pageData.getTotalPages())
        .totalElements(pageData.getTotalElements())
        .pageSizes(p.getLimit())
        .success(true)
        .message("Search successfully.")
        .result(pageData.getContent().stream().map(TourMapper::tourToTourSearchResponse).toList())
        .build();
    this.redisService.saveKeyAndValue(keyCache , response , "2" , TimeUnit.MINUTES);
    return response;
  }

  @Override
  public PageResponse<?> searchByAdmin(TourSearchByAdminParams p) {
    String keyCache = String.format(
        "search:tour:admin:page:%d:limit:%d:name:%s:city:%s:owner:%s",
        p.getPage(),
        p.getLimit(),
        p.getTourName() == null ? "" : p.getTourName(),
        p.getTourCity() == null ? "" : p.getTourCity(),
        p.getOwner() == null ? "" : p.getOwner()
    );

    PageResponse<TourSearchResponse> dataCache = this.redisService.getValue(
        keyCache,
        new TypeReference<PageResponse<TourSearchResponse>>() {}
    );

    if (dataCache != null) {
      return dataCache;
    }

    Specification<Tour> spec = Specification.where(null);

    // Filter theo tên tour
    if (p.getTourName() != null && !p.getTourName().isEmpty()) {
      spec = spec.and((root, query, cb) ->
          cb.like(cb.lower(root.get("name")), "%" + p.getTourName().toLowerCase() + "%")
      );
    }

    // Filter theo thành phố
    if (p.getTourCity() != null && !p.getTourCity().isEmpty()) {
      spec = spec.and((root, query, cb) ->
          cb.like(cb.lower(root.get("city")), "%" + p.getTourCity().toLowerCase() + "%")
      );
    }

    // Filter theo owner (username hoặc email tuỳ logic)
    if (p.getOwner() != null && !p.getOwner().isEmpty()) {
      spec = spec.and((root, query, cb) ->
          cb.like(cb.lower(root.get("owner").get("username")),
              "%" + p.getOwner().toLowerCase() + "%")
      );
    }

    Pageable pageable = PageRequest.of(p.getPage() - 1, p.getLimit());

    Page<Tour> pageData = tourRepository.findAll(spec, pageable);

    PageResponse<TourSearchResponse> response =
        PageResponse.<TourSearchResponse>builder()
            .currentPages(p.getPage())
            .totalPages(pageData.getTotalPages())
            .totalElements(pageData.getTotalElements())
            .pageSizes(p.getLimit())
            .success(true)
            .message("Search successfully.")
            .result(pageData.getContent()
                .stream()
                .map(TourMapper::tourToTourSearchResponse)
                .toList())
            .build();

    this.redisService.saveKeyAndValue(keyCache, response, "2", TimeUnit.MINUTES);
    return response;
  }


  @Override
  public ApiResponse<?> searchTourDetails(Long tourId) {
    String keyCache = String.format("search:tour:details:%d" , tourId);
    ApiResponse<TourResponse> dataCache = this.redisService.getValue(keyCache,
        new TypeReference<ApiResponse<TourResponse>>() {});
    if (dataCache != null) {
      return dataCache;
    }

    Tour tour = this.tourRepository.findById(tourId).orElseThrow(() -> new ResourceNotFoundException("Tour not found"));
    ApiResponse<TourResponse> response = ApiResponse.<TourResponse>builder()
        .data(TourMapper.tourToTourResponse(tour))
        .success(true)
        .message("Search successfully.")
        .build();
    this.redisService.saveKeyAndValue(keyCache , response , "2" , TimeUnit.MINUTES);
    return response;
  }

  @Override
  public ApiResponse<?> infoTourDetails(Long tourId) {
    String keyCache = String.format("search:tour:info:%d" , tourId);
    ApiResponse<InfoTourDetails> dataCache = this.redisService.getValue(keyCache,
        new TypeReference<ApiResponse<InfoTourDetails>>() {});
    if (dataCache != null) {
      return dataCache;
    }
    Tour tour = this.tourRepository.findById(tourId).orElseThrow(() -> new ResourceNotFoundException("Tour not found"));
    ApiResponse<InfoTourDetails> response = ApiResponse.<InfoTourDetails>builder()
        .success(true)
        .message("Search details successfully.")
        .data(TourMapper.toInfoTourDetails(tour))
        .build();
    this.redisService.saveKeyAndValue(keyCache , response , "2" , TimeUnit.MINUTES);
    return response;
  }

  @Override
  public ApiResponse<?> tourBookingInfo(Long tourId) {
    Tour tour = this.tourRepository.findById(tourId).orElseThrow(() -> new ResourceNotFoundException("Tour not found"));
    return ApiResponse.builder()
        .success(true)
        .data(TourMapper.toInfoTourDetailsBooking(tour))
        .message("Info tour booking..")
        .build();
  }

  @Override
  public ApiResponse<?> deleteTour(Long tourId) {
    Tour tour = this.tourRepository.findById(tourId).orElseThrow(() -> new ResourceNotFoundException("Tour not found"));
    List<ImageTour> imageTours = tour.getImages();
    for (ImageTour imageTour : imageTours) {
      this.cloudinaryService.deleteImage(imageTour.getPublicId());
    }
    User owner = tour.getOwner();
    owner.getTours().remove(tour);
    this.userRepository.save(owner);
    return ApiResponse.builder().success(true).message("Deleted successfully.").build();
  }
}
