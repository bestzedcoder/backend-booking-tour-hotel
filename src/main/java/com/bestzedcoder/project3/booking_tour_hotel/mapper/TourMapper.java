package com.bestzedcoder.project3.booking_tour_hotel.mapper;

import com.bestzedcoder.project3.booking_tour_hotel.dto.response.InfoHotelDetails;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.InfoTourDetails;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.TourResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.TourScheduleResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.TourSearchResponse;
import com.bestzedcoder.project3.booking_tour_hotel.model.ImageTour;
import com.bestzedcoder.project3.booking_tour_hotel.model.Tour;
import com.bestzedcoder.project3.booking_tour_hotel.model.TourSchedule;
import com.bestzedcoder.project3.booking_tour_hotel.model.User;

public class TourMapper {
  public static TourSearchResponse tourToTourSearchResponse(Tour tour) {
    TourSearchResponse tourSearchResponse = new TourSearchResponse();
    tourSearchResponse.setTourId(tour.getId());
    tourSearchResponse.setTourName(tour.getName());
    tourSearchResponse.setTourDescription(tour.getDescription());
    tourSearchResponse.setTourPrice(tour.getPrice());
    tourSearchResponse.setTourDuration(tour.getDuration());
    tourSearchResponse.setTourCity(tour.getCity());
    tourSearchResponse.setTourMaxPeople(tour.getMaxPeople());
    tourSearchResponse.setTourStart(tour.getStartDate());
    tourSearchResponse.setTourEnd(tour.getEndDate());
    tourSearchResponse.setTourImageUrl(tour.getImages().get(0).getUrl());
    return tourSearchResponse;
  }

  public static TourResponse tourToTourResponse(Tour tour) {
    TourResponse tourResponse = new TourResponse();
    tourResponse.setTourId(tour.getId());
    tourResponse.setTourName(tour.getName());
    tourResponse.setTourDescription(tour.getDescription());
    tourResponse.setTourPrice(tour.getPrice());
    tourResponse.setDuration(tour.getDuration());
    tourResponse.setTourCity(tour.getCity());
    tourResponse.setMaxPeople(tour.getMaxPeople());
    tourResponse.setStartDate(tour.getStartDate());
    tourResponse.setEndDate(tour.getEndDate());
    tourResponse.setImageTourUrls(tour.getImages().stream().map(ImageTour::getUrl).toList());
    tourResponse.setTourSchedules(tour.getSchedules().stream().map(TourMapper::toTourScheduleResponse).toList());
    return tourResponse;
  }

  private static TourScheduleResponse toTourScheduleResponse(TourSchedule tourSchedule) {
    TourScheduleResponse tourScheduleResponse = new TourScheduleResponse();
    tourScheduleResponse.setTourScheduleId(tourSchedule.getId());
    tourScheduleResponse.setDescription(tourSchedule.getDescription());
    tourScheduleResponse.setTitle(tourSchedule.getTitle());
    return tourScheduleResponse;
  }

  public static InfoTourDetails toInfoTourDetails(Tour tour) {
    InfoTourDetails infoTourDetails = new InfoTourDetails();
    infoTourDetails.setTourId(tour.getId());
    infoTourDetails.setTourName(tour.getName());
    infoTourDetails.setTourDescription(tour.getDescription());
    infoTourDetails.setTourPrice(tour.getPrice());
    infoTourDetails.setTourCity(tour.getCity());
    infoTourDetails.setDuration(tour.getDuration());
    infoTourDetails.setMaxPeople(tour.getMaxPeople());
    infoTourDetails.setStartDate(tour.getStartDate());
    infoTourDetails.setEndDate(tour.getEndDate());
    infoTourDetails.setImageTourUrls(tour.getImages().stream().map(ImageTour::getUrl).toList());
    infoTourDetails.setTourSchedules(tour.getSchedules().stream().map(TourMapper::toTourScheduleResponse).toList());
    User owner = tour.getOwner();
    InfoTourDetails.InfoOwner ownerInfo = new InfoTourDetails.InfoOwner();
    ownerInfo.setFullName(owner.getProfile().getFullName());
    ownerInfo.setPhoneNumber(owner.getProfile().getPhoneNumber());
    infoTourDetails.setOwner(ownerInfo);
    return infoTourDetails;
  }
}
