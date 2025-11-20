package com.bestzedcoder.project3.booking_tour_hotel.model;

import com.bestzedcoder.project3.booking_tour_hotel.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "_tour_booking")
public class TourBooking extends BaseEntity {
  @OneToOne
  @JoinColumn(name = "booking_id")
  private Booking booking;

  @ManyToOne
  @JoinColumn(name = "tour_id")
  private Tour tour;

  @Column(name = "tour_name" , nullable = false)
  private String tourName;

  @Column(nullable = false)
  private int people;

  @Column(nullable = false)
  private int duration;

  @Column(name = "start_date" , nullable = false)
  private LocalDate startDate;

  @Column(name = "end_date" , nullable = false)
  private LocalDate endDate;
}
