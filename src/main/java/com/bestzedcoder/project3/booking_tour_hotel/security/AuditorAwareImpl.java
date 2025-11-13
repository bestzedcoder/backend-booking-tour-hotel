package com.bestzedcoder.project3.booking_tour_hotel.security;

import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditorAwareImpl implements AuditorAware<String> {

  @Override
  public Optional<String> getCurrentAuditor() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal != null && principal instanceof User user) {
      String username = ((User) principal).getUsername();
      return Optional.of(username);
    }
    return Optional.of("system");
  }
}
