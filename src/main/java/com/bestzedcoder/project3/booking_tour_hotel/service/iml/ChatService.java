package com.bestzedcoder.project3.booking_tour_hotel.service.iml;

import com.bestzedcoder.project3.booking_tour_hotel.dto.requests.ChatMessageRequest;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ApiResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.ConversationResponse;
import com.bestzedcoder.project3.booking_tour_hotel.dto.response.UserStatusResponse;
import com.bestzedcoder.project3.booking_tour_hotel.exception.ResourceNotFoundException;
import com.bestzedcoder.project3.booking_tour_hotel.mapper.ChatMapper;
import com.bestzedcoder.project3.booking_tour_hotel.model.Chat;
import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import com.bestzedcoder.project3.booking_tour_hotel.repository.ChatRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.UserRepository;
import com.bestzedcoder.project3.booking_tour_hotel.service.IChatService;
import com.bestzedcoder.project3.booking_tour_hotel.ws.UserStatusService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService implements IChatService {
  private final UserStatusService userStatusService;
  private final UserRepository userRepository;
  private final SimpMessagingTemplate messagingTemplate;
  private final ChatRepository chatRepository;

  @Override
  public void sendMessage(ChatMessageRequest message) {
    message.setTimestamp(LocalDateTime.now().toString());
    messagingTemplate.convertAndSend("/topic/messages/" + message.getReceiverId() , message);
    Chat chat = new Chat();
    chat.setContent(message.getContent());
    chat.setSenderId(Long.parseLong(message.getSenderId()));
    chat.setReceiverId(Long.parseLong(message.getReceiverId()));
    this.chatRepository.save(chat);
  }

  @Override
  public ApiResponse<?> checkUserStatus(String email) {
    User user = this.userRepository.findByEmail(email);
    if (user == null) {
      throw new ResourceNotFoundException("Không tìm thấy business có email là: " + email);
    }
    UserStatusResponse response = new UserStatusResponse();
    response.setUserId(user.getId());
    response.setOnline(this.userStatusService.isUserOnline(String.valueOf(user.getId())));
    if(user.getProfile().getImage() != null) {
      response.setImageUrl(user.getProfile().getImage().getUrl());
    }
    response.setFullName(user.getProfile().getFullName());
    return ApiResponse.builder()
        .success(true)
        .message("success")
        .data(response)
        .build();
  }

  @Override
  public ApiResponse<?> getChat(Long senderId, Long receiverId) {
    List<Chat> messages = this.chatRepository.findFullConversation(senderId , receiverId);
    return ApiResponse.builder()
        .success(true)
        .message("success")
        .data(messages.stream().map(ChatMapper::toMessagesResponse).toList())
        .build();
  }

  @Override
  public ApiResponse<?> getConversations(Long userId) {
    List<Chat> lastMessages = chatRepository.findLastMessagesByUser(userId);

    List<ConversationResponse> list = lastMessages.stream().map(chat -> {
      Long partnerId = chat.getSenderId().equals(userId) ? chat.getReceiverId() : chat.getSenderId();
      User partner = userRepository.findById(partnerId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));

      return ConversationResponse.builder()
          .userId(partnerId)
          .fullName(partner.getProfile().getFullName())
          .imageUrl(partner.getProfile().getImage() != null ? partner.getProfile().getImage().getUrl() : null)
          .lastMessage(chat.getContent())
          .timestamp(chat.getCreatedAt())
          .online(userStatusService.isUserOnline(String.valueOf(partnerId)))
          .build();
    }).toList();

    return ApiResponse.builder()
        .success(true)
        .message("success")
        .data(list)
        .build();
  }

  @Override
  public ApiResponse<?> getUserSummary(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    ConversationResponse response = ConversationResponse.builder()
        .userId(user.getId())
        .fullName(user.getProfile().getFullName())
        .imageUrl(user.getProfile().getImage() != null ? user.getProfile().getImage().getUrl() : null)
        .online(userStatusService.isUserOnline(String.valueOf(userId)))
        .build();

    return ApiResponse.builder()
        .success(true)
        .message("success")
        .data(response)
        .build();
  }
}
