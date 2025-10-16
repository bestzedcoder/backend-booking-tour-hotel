package com.bestzedcoder.project3.booking_tour_hotel.model;

import com.bestzedcoder.project3.booking_tour_hotel.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "_profile")
@EntityListeners(AuditingEntityListener.class)
public class Profile extends BaseEntity {
  private String fullName;
  private String phoneNumber;
  private String address;

  @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Image> images = new HashSet<>();

  @OneToOne
  @JsonIgnore
  @JoinColumn(name = "user_id")
  private User user;
}
