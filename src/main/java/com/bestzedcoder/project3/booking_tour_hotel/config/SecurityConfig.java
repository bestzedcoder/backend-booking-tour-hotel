package com.bestzedcoder.project3.booking_tour_hotel.config;

import com.bestzedcoder.project3.booking_tour_hotel.security.CustomizeAuthenticationProvider;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  @Bean
  public SecurityFilterChain configure(HttpSecurity http) throws Exception {
    http
//        .cors(customizeCors -> customizeCors.configurationSource(
//            new CorsConfigurationSource() {
//              @Override
//              public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
//                CorsConfiguration config = new CorsConfiguration();
//                config.setAllowedOrigins(List.of("*"));
//                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
//                config.setExposedHeaders(List.of("Access-Control-Allow-Origin", "Access-Control-Allow-Headers"));
//                config.setAllowedHeaders(List.of("Access-Control-Allow-Headers"));
//                return config;
//              }
//            }
//        ))
        .cors(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(requests ->requests.requestMatchers("/auth/**").permitAll()
        .anyRequest().authenticated());
//            .addFilterBefore(new JwtValidationFilter() , UsernamePasswordAuthenticationFilter.class)
//        .authenticationProvider();
//    http.formLogin(AbstractHttpConfigurer::disable);
//    http.httpBasic(AbstractHttpConfigurer::disable);
    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
      PasswordEncoder passwordEncoder) {
    CustomizeAuthenticationProvider authenticationProvider =
        new CustomizeAuthenticationProvider(passwordEncoder, userDetailsService);
    ProviderManager providerManager = new ProviderManager(authenticationProvider);
    providerManager.setEraseCredentialsAfterAuthentication(false);
    return  providerManager;
  }
}
