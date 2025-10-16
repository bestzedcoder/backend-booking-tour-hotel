package com.bestzedcoder.project3.booking_tour_hotel.security;

import com.bestzedcoder.project3.booking_tour_hotel.exception.UnauthorizedException;
import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class JwtUtils {
  public String JwtGenerator(User user,String secret,String expiration) {
    Instant now = Instant.now();
    Date issuedAt = Date.from(now);
    Date expiryDate = Date.from(now.plusMillis(Long.parseLong(expiration)));
    return Jwts.builder().setIssuer("Security").setSubject("JWT Token")
        .claim("userId",user.getId())
        .claim("username" , user.getUsername())
        .claim("authorities" , user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(
            Collectors.joining(",")))
        .setIssuedAt(issuedAt)
        .setExpiration(expiryDate)
        .signWith(this.getSecretKey(secret), SignatureAlgorithm.HS256)
        .compact();
  }
  private SecretKey getSecretKey(String secret) {
    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }


  public Claims extractClaims(String token, String secret) {
    try {
      return Jwts.parserBuilder()
          .setSigningKey(getSecretKey(secret))
          .build()
          .parseClaimsJws(token)
          .getBody();
    } catch (ExpiredJwtException ex) {
      throw new UnauthorizedException("Token is expired");
    } catch (JwtException ex) {
      throw new UnauthorizedException("Token is invalid");
    }
  }

  public boolean isTokenExpired(String token, String secret) {
    try {
      Claims claims = extractClaims(token, secret);
      Date expiration = claims.getExpiration();
      return expiration.before(new Date());
    } catch (ExpiredJwtException ex) {
      throw new UnauthorizedException("Token is expired");
    } catch (JwtException ex) {
      throw new UnauthorizedException("Token is invalid");
    }
  }

}
