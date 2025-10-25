package com.bestzedcoder.project3.booking_tour_hotel.mapper;

import com.bestzedcoder.project3.booking_tour_hotel.dto.response.HotelResponse;
import com.bestzedcoder.project3.booking_tour_hotel.model.Hotel;
import com.bestzedcoder.project3.booking_tour_hotel.model.ImageHotel;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class HotelMapper {
  public static HotelResponse hotelToHotelResponse(Hotel hotel) {
    HotelResponse hotelResponse = new HotelResponse();
    hotelResponse.setHotelName(hotel.getHotel_name());
    hotelResponse.setHotelCity(hotel.getHotel_city());
    hotelResponse.setHotelAddress(hotel.getHotel_address());
    hotelResponse.setHotelDescription(hotel.getHotel_description());
    hotelResponse.setHotelPhone(hotel.getOwner().getProfile().getPhoneNumber());
    hotelResponse.setHotelImages(
        hotel.getImages().stream().map(ImageHotel::getUrl).toArray(String[]::new));
    return hotelResponse;
  }
}
