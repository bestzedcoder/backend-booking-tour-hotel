package com.bestzedcoder.project3.booking_tour_hotel.mapper;

import com.bestzedcoder.project3.booking_tour_hotel.dto.response.HotelAndRoomDetailsBooking;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.HotelResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.HotelSearchResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.InfoHotelDetails;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.RoomResponse;
import com.bestzedcoder.project3.booking_tour_hotel.model.Hotel;
import com.bestzedcoder.project3.booking_tour_hotel.model.ImageHotel;
import com.bestzedcoder.project3.booking_tour_hotel.model.Room;
import org.springframework.stereotype.Component;

@Component
public class HotelMapper {
  public static HotelResponse hotelToHotelResponse(Hotel hotel) {
    HotelResponse hotelResponse = new HotelResponse();
    hotelResponse.setHotelId(hotel.getId());
    hotelResponse.setHotelName(hotel.getHotelName());
    hotelResponse.setHotelCity(hotel.getHotelCity());
    hotelResponse.setHotelAddress(hotel.getHotelAddress());
    hotelResponse.setHotelDescription(hotel.getHotelDescription());
    hotelResponse.setHotelPhone(hotel.getOwner().getProfile().getPhoneNumber());
    hotelResponse.setHotelStar(hotel.getHotelStar());
    hotelResponse.setHotelImages(
        hotel.getImages().stream().map(ImageHotel::getUrl).toList());
    hotelResponse.setRooms(hotel.getRooms().stream().map(HotelMapper::roomToRoomResponse).toList());
    return hotelResponse;
  }

  public static HotelSearchResponse hotelToHotelSearchResponse(Hotel hotel) {
    HotelSearchResponse hotelSearchResponse = new HotelSearchResponse();
    hotelSearchResponse.setHotelId(hotel.getId());
    hotelSearchResponse.setHotelName(hotel.getHotelName());
    hotelSearchResponse.setCity(hotel.getHotelCity());
    hotelSearchResponse.setAddress(hotel.getHotelAddress());
    hotelSearchResponse.setDescription(hotel.getHotelDescription());
    hotelSearchResponse.setStar(hotel.getHotelStar());
    if((hotel.getImages() != null) && (!hotel.getImages().isEmpty())) {
        String urlImageMain = hotel.getImages().stream().findFirst().get().getUrl();
        hotelSearchResponse.setImageUrl(urlImageMain);
    }
    return hotelSearchResponse;
  }

  private static RoomResponse roomToRoomResponse(Room room) {
    RoomResponse roomResponse = new RoomResponse();
    roomResponse.setRoomId(room.getId());
    roomResponse.setRoomName(room.getRoomName());
    roomResponse.setRoomType(room.getType());
    roomResponse.setPricePerDay(room.getPricePerDay());
    roomResponse.setPricePerHour(room.getPricePerHour());
    roomResponse.setStatus(room.getStatus());
    return roomResponse;
  }

  public static InfoHotelDetails hotelToHotelDetails(Hotel hotel) {
    InfoHotelDetails details = new InfoHotelDetails();
    details.setHotelId(hotel.getId());
    details.setHotelName(hotel.getHotelName());
    details.setHotelCity(hotel.getHotelCity());
    details.setHotelDescription(hotel.getHotelDescription());
    details.setHotelAddress(hotel.getHotelAddress());
    details.setHotelPhone(hotel.getOwner().getProfile().getPhoneNumber());
    details.setHotelStar(hotel.getHotelStar());

    if (hotel.getImages() != null && !hotel.getImages().isEmpty()) {
      details.setHotelImages(
          hotel.getImages().stream()
              .map(ImageHotel::getUrl)
              .toList()
      );
    }

    if (hotel.getRooms() != null && !hotel.getRooms().isEmpty()) {
      details.setRooms(
          hotel.getRooms().stream()
              .map(HotelMapper::roomToRoomResponse)
              .toList()
      );
    }
    InfoHotelDetails.InfoOwner ownerInfo = new InfoHotelDetails.InfoOwner();
    ownerInfo.setFullName(hotel.getOwner().getProfile().getFullName());
    ownerInfo.setPhoneNumber(hotel.getOwner().getProfile().getPhoneNumber());
    details.setOwner(ownerInfo);

    return details;
  }

  public static HotelAndRoomDetailsBooking hotelToHotelAndRoomDetailsBooking(Hotel hotel, Room room) {
    HotelAndRoomDetailsBooking response = new HotelAndRoomDetailsBooking();
    response.setHotelId(hotel.getId());
    response.setRoomId(room.getId());
    response.setHotelName(hotel.getHotelName());
    response.setRoomName(room.getRoomName());
    response.setHotelAddress(hotel.getHotelAddress());
    response.setHotelCity(hotel.getHotelCity());
    response.setHotelPhone(hotel.getOwner().getProfile().getPhoneNumber());
    response.setPricePerDay(room.getPricePerDay());
    response.setPricePerHour(room.getPricePerHour());
    return response;
  }
}
