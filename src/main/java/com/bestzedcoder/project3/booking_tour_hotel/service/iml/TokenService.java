package com.bestzedcoder.project3.booking_tour_hotel.service.iml;

import com.bestzedcoder.project3.booking_tour_hotel.exception.UnauthorizedException;
import com.bestzedcoder.project3.booking_tour_hotel.model.Token;
import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import com.bestzedcoder.project3.booking_tour_hotel.repository.TokenRepository;
import com.bestzedcoder.project3.booking_tour_hotel.service.ITokenService;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService implements ITokenService {
  private final TokenRepository tokenRepository;
  @Override
  public String generateAndSaveToken(User user) {
    String generatedToken = this.generateActivationCode(8);
    var token = Token.builder().token(generatedToken)
        .createdAt(LocalDateTime.now())
        .expiresAt(LocalDateTime.now().plusMinutes(15))
        .user(user)
        .build();
    this.tokenRepository.save(token);
    return generatedToken;
  }

  @Override
  public boolean check(String token) {
    Token t = this.tokenRepository.findByToken(token).orElseThrow(()->{
      throw new UnauthorizedException("Token invalid");
    });
    System.out.println("time now: " + LocalDateTime.now());
    System.out.println("time expires: " + t.getExpiresAt());
    if (LocalDateTime.now().isAfter(t.getExpiresAt())) {
      return false;
    }
    return true;
  }

  private String generateActivationCode(int length) {
    String characters = "0123456789";
    StringBuilder codeBuilder = new StringBuilder();
    SecureRandom random = new SecureRandom();
    for (int i = 0; i < length; i++) {
      int randomIndex = random.nextInt(characters.length());
      codeBuilder.append(characters.charAt(randomIndex));
    }
    return codeBuilder.toString();
  }
}
