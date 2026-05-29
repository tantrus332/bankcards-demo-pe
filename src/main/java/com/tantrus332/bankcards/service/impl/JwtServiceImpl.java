package com.tantrus332.bankcards.service.impl;

import com.tantrus332.bankcards.config.JwtProperties;
import com.tantrus332.bankcards.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class JwtServiceImpl implements JwtService {
  private final JwtProperties props;

  @Override
  public String extractUsername(String token) {
    return extractPayload(token).getSubject();
  }

  @Override
  public String generateToken(UserDetails userDetails) {
    return buildToken(userDetails);
  }

  @Override
  public boolean isTokenValid(String token, UserDetails userDetails) {
    String username = extractUsername(token);
    return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }

  private String buildToken(UserDetails userDetails) {
    return Jwts.builder()
        .subject(userDetails.getUsername())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + props.getExpiration()))
        .signWith(getSigningKey(), Jwts.SIG.HS256)
        .compact();
  }

  private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(props.getSecretKey());
    return Keys.hmacShaKeyFor(keyBytes);
  }

  private Claims extractPayload(String token) {
    return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractPayload(token).getExpiration();
  }
}
