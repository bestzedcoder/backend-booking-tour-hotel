package com.bestzedcoder.project3.booking_tour_hotel.service;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.TourCreatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.TourScheduleUpdatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.TourSearchByAdminParams;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.TourSearchParams;
import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.TourUpdatingRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.PageResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ITourService {
  ApiResponse<?> createTour(TourCreatingRequest tourCreatingRequest, MultipartFile[] images);
  ApiResponse<?> updateTour(Long id, TourUpdatingRequest tourUpdatingRequest, MultipartFile[] imageNews);
  ApiResponse<?> updateTourSchedule(Long tourId, Long tourScheduledId,
      TourScheduleUpdatingRequest tourScheduleUpdatingRequest);
  PageResponse<?> searchByUser(TourSearchParams tourSearchParams);
  PageResponse<?> searchByOwner(TourSearchParams tourSearchParams);
  PageResponse<?> searchByAdmin(TourSearchByAdminParams tourSearchByAdminParams);
  ApiResponse<?> searchTourDetails(Long tourId);
  ApiResponse<?> infoTourDetails(Long tourId);
  ApiResponse<?> deleteTour(Long tourId);
}
