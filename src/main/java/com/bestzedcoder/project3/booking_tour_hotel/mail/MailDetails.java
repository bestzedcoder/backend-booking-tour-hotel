package com.bestzedcoder.project3.booking_tour_hotel.mail;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class MailDetails {
  private String to;
  private String token;
  private String username;
  private String fullName;
  private String rawPassword;
}
