package com.bestzedcoder.project3.booking_tour_hotel.config;

import com.bestzedcoder.project3.booking_tour_hotel.exception.CustomAccessDeniedHandler;
import com.bestzedcoder.project3.booking_tour_hotel.exception.CustomAuthenticationEntryPoint;
import com.bestzedcoder.project3.booking_tour_hotel.security.CustomizeAuthenticationProvider;
import com.bestzedcoder.project3.booking_tour_hotel.security.JwtValidationFilter;
import com.bestzedcoder.project3.booking_tour_hotel.security.OAuth2SuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
  private final JwtValidationFilter jwtValidationFilter;
  private final OAuth2SuccessHandler oAuth2SuccessHandler;
  @Bean
  public SecurityFilterChain configure(HttpSecurity http) throws Exception {
    http
        .anonymous(AbstractHttpConfigurer::disable)
        .cors(customizeCors -> customizeCors.configurationSource(
            new CorsConfigurationSource() {
              @Override
              public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(List.of("http://localhost:5173"));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);
                return config;
              }
            }
        ))
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(requests ->requests
            .requestMatchers("/auth/login","/auth/register","/auth/verify" , "/auth/refresh" ,  "/swagger-ui/**",
                "/v3/api-docs/**","/payment/vn-pay-callback").permitAll()
        .anyRequest().authenticated())
        .addFilterBefore(jwtValidationFilter, UsernamePasswordAuthenticationFilter.class)
        .oauth2Login(oauth2 -> oauth2.successHandler(oAuth2SuccessHandler));

      http.exceptionHandling(exception ->
        exception.authenticationEntryPoint(authenticationEntryPoint())
            .accessDeniedHandler(accessDeniedHandler())
      );
    return http.build();
  }

  @Bean
  public CustomAccessDeniedHandler accessDeniedHandler() {
    return new CustomAccessDeniedHandler();
  }

  @Bean
  public CustomAuthenticationEntryPoint authenticationEntryPoint() {
    return new CustomAuthenticationEntryPoint();
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
