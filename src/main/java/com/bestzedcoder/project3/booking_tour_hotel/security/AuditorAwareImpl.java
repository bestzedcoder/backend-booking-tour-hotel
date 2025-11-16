package com.bestzedcoder.project3.booking_tour_hotel.security;

import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditorAwareImpl implements AuditorAware<String> {

  @Override
  public Optional<String> getCurrentAuditor() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()) {
      Object principal = authentication.getPrincipal();
      if (principal instanceof User user) {
        String username = user.getUsername();
        return Optional.of(username);
      }
    }
    return Optional.of("system");
  }
}
