package com.bestzedcoder.project3.booking_tour_hotel.mapper;

import com.bestzedcoder.project3.booking_tour_hotel.dto.response.MessagesResponse;
import com.bestzedcoder.project3.booking_tour_hotel.model.Chat;

public class ChatMapper {
  public static MessagesResponse toMessagesResponse(Chat chat) {
    MessagesResponse message = new MessagesResponse();
    message.setContent(chat.getContent());
    message.setSenderId(chat.getSenderId());
    message.setReceiverId(chat.getReceiverId());
    message.setTimestamp(chat.getCreatedAt());
    return message;
  }
}
