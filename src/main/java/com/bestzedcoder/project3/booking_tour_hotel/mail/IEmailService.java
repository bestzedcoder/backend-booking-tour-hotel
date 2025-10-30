package com.bestzedcoder.project3.booking_tour_hotel.mail;

public interface IEmailService {
  void sendVerificationEmail(MailDetails mailDetails);
  void sendInfoUserDetails(MailDetails mailDetails);
}
