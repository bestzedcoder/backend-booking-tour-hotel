package com.bestzedcoder.project3.booking_tour_hotel.model;

import com.bestzedcoder.project3.booking_tour_hotel.common.BaseEntity;
import com.bestzedcoder.project3.booking_tour_hotel.enums.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "_payment")
public class Payment extends BaseEntity {
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "booking_id" , nullable = false)
  private Booking booking;

  @Column(name = "payment_amount")
  private Double amount;

  @Column(name = "payment_status")
  @Enumerated(EnumType.STRING)
  private PaymentStatus status;

  @Column(name = "transaction_no")
  private String transactionNo;        // vnp_TransactionNo

  @Column(name = "bank_code")
  private String bankCode;

  @Column(name = "pay_date")
  private String payDate;              // vnp_PayDate

  @Column(name = "order_info")
  private String orderInfo;

  @Column(name = "payment_url", columnDefinition = "TEXT")
  private String paymentUrl;
}
