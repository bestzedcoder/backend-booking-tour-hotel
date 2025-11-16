package com.bestzedcoder.project3.booking_tour_hotel.model;

import com.bestzedcoder.project3.booking_tour_hotel.common.BaseEntity;
import com.bestzedcoder.project3.booking_tour_hotel.event.TourEvent;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
@EntityListeners(TourEvent.class)
@Table(name = "_tour")
public class Tour extends BaseEntity {
  @Column(nullable = false, length = 150)
  private String name;

  @Column(nullable = false)
  private String city;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false)
  private Double price;

  @Column(name = "start_date", nullable = false)
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate startDate;

  @Column(name = "end_date", nullable = false)
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate endDate;

  @Column(nullable = false)
  private Integer duration;

  @Column(name = "max_people")
  private Integer maxPeople;

  @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TourSchedule> schedules = new ArrayList<>();

  @OneToMany(mappedBy = "tour" , cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ImageTour> images = new ArrayList<>();

  @ManyToOne
  @JoinColumn(name = "owner_id")
  private User owner;
}
