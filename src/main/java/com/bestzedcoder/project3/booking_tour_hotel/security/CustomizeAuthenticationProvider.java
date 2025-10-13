package com.bestzedcoder.project3.booking_tour_hotel.security;

import com.bestzedcoder.project3.booking_tour_hotel.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomizeAuthenticationProvider implements AuthenticationProvider {
  private final PasswordEncoder passwordEncoder;
  private final UserDetailsService userDetailsService;
  @Override
  public Authentication authenticate(Authentication authentication) throws UnauthorizedException {
    String username = authentication.getName();
    String password = authentication.getCredentials().toString();
    UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
    if(this.passwordEncoder.matches(password, userDetails.getPassword())) {
      return new UsernamePasswordAuthenticationToken(userDetails , password , userDetails.getAuthorities());
    } else {
      throw new UnauthorizedException("Invalid password");
    }
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }
}
