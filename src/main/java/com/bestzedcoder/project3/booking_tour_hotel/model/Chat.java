package com.bestzedcoder.project3.booking_tour_hotel.model;

import com.bestzedcoder.project3.booking_tour_hotel.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "_chat")
public class Chat extends BaseEntity {
  @Column(name = "chat_message" , nullable = false)
  private String content;
  @Column(name = "chat_sender" , nullable = false)
  private Long senderId;
  @Column(name = "chat_receiver" , nullable = false)
  private Long receiverId;
}
