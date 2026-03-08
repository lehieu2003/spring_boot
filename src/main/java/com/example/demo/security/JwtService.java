package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

  @Value("${app.jwt.secret}")
  private String jwtSecret;

  @Value("${app.jwt.access-token-expiration}")
  private long accessTokenExpiration;

  @Value("${app.jwt.refresh-token-expiration}")
  private long refreshTokenExpiration;

  public String generateAccessToken(UserDetails userDetails) {
    return buildToken(userDetails, accessTokenExpiration, Map.of("type", "access"));
  }

  public String generateRefreshToken(UserDetails userDetails) {
    return buildToken(userDetails, refreshTokenExpiration, Map.of("type", "refresh"));
  }

  public String extractUsername(String token) {
    return parseClaims(token).getSubject();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return parseClaims(token).getExpiration().before(new Date());
  }

  private String buildToken(UserDetails userDetails, long expiration, Map<String, Object> extraClaims) {
    return Jwts.builder()
        .claims(extraClaims)
        .subject(userDetails.getUsername())
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSigningKey())
        .compact();
  }

  private Claims parseClaims(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public long getRefreshTokenExpiration() {
    return refreshTokenExpiration;
  }
}
