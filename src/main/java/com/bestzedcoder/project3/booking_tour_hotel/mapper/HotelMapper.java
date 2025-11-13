package com.bestzedcoder.project3.booking_tour_hotel.mapper;

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
    hotelResponse.setHotelName(hotel.getHotel_name());
    hotelResponse.setHotelCity(hotel.getHotel_city());
    hotelResponse.setHotelAddress(hotel.getHotel_address());
    hotelResponse.setHotelDescription(hotel.getHotel_description());
    hotelResponse.setHotelPhone(hotel.getOwner().getProfile().getPhoneNumber());
    hotelResponse.setHotelStar(hotel.getHotel_star());
    hotelResponse.setHotelImages(
        hotel.getImages().stream().map(ImageHotel::getUrl).toArray(String[]::new));
    hotelResponse.setRooms(hotel.getRooms().stream().map(HotelMapper::roomToRoomResponse).toArray(RoomResponse[]::new));
    return hotelResponse;
  }

  public static HotelSearchResponse hotelToHotelSearchResponse(Hotel hotel) {
    HotelSearchResponse hotelSearchResponse = new HotelSearchResponse();
    hotelSearchResponse.setHotelId(hotel.getId());
    hotelSearchResponse.setHotelName(hotel.getHotel_name());
    hotelSearchResponse.setCity(hotel.getHotel_city());
    hotelSearchResponse.setAddress(hotel.getHotel_address());
    hotelSearchResponse.setDescription(hotel.getHotel_description());
    hotelSearchResponse.setStar(hotel.getHotel_star());
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
    details.setHotelName(hotel.getHotel_name());
    details.setHotelCity(hotel.getHotel_city());
    details.setHotelDescription(hotel.getHotel_description());
    details.setHotelAddress(hotel.getHotel_address());
    details.setHotelPhone(hotel.getOwner().getProfile().getPhoneNumber());
    details.setHotelStar(hotel.getHotel_star());

    if (hotel.getImages() != null && !hotel.getImages().isEmpty()) {
      details.setHotelImages(
          hotel.getImages().stream()
              .map(ImageHotel::getUrl)
              .toArray(String[]::new)
      );
    } else {
      details.setHotelImages(new String[0]);
    }

    if (hotel.getRooms() != null && !hotel.getRooms().isEmpty()) {
      details.setRooms(
          hotel.getRooms().stream()
              .map(HotelMapper::roomToRoomResponse)
              .toArray(RoomResponse[]::new)
      );
    } else {
      details.setRooms(new RoomResponse[0]);
    }

    InfoHotelDetails.InfoOwner ownerInfo = new InfoHotelDetails.InfoOwner();
    ownerInfo.setFullName(hotel.getOwner().getProfile().getFullName());
    ownerInfo.setPhoneNumber(hotel.getOwner().getProfile().getPhoneNumber());
    details.setOwner(ownerInfo);

    return details;
  }
}
