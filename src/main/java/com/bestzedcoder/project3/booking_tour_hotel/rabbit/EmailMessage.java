package com.bestzedcoder.project3.booking_tour_hotel.rabbit;

import com.bestzedcoder.project3.booking_tour_hotel.enums.EmailType;
import com.bestzedcoder.project3.booking_tour_hotel.mail.ContentInvoiceHotel;
import com.bestzedcoder.project3.booking_tour_hotel.mail.ContentInvoiceTour;
import com.bestzedcoder.project3.booking_tour_hotel.mail.MailDetails;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailMessage {
  private EmailType MessageType;
  private MailDetails mailDetails;
  private ContentInvoiceTour contentInvoiceTour;
  private ContentInvoiceHotel contentInvoiceHotel;
}
