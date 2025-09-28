package com.saqib.school.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@Slf4j
@Getter
public class JwtService {

  private final SecretKey secretKey;
  private final long accessTokenExpiration;
  private final long refreshTokenExpiration;

  public JwtService(@Value("${jwt.secret}") String secret,
                    @Value("${jwt.expiration}") long accessTokenExpiration,
                    @Value("${jwt.refresh-expiration}") long refreshTokenExpiration) {
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    this.accessTokenExpiration = accessTokenExpiration;
    this.refreshTokenExpiration = refreshTokenExpiration;
  }

  public String generateAccessToken(Authentication authentication) {
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

    Instant now = Instant.now();
    Instant expiryDate = now.plus(accessTokenExpiration, ChronoUnit.MILLIS);

    String authorities = authentication.getAuthorities().stream()
      .map(GrantedAuthority::getAuthority)
      .collect(Collectors.joining(","));

    return Jwts.builder()
      .subject(userPrincipal.getUsername())
      .issuedAt(Date.from(now))
      .expiration(Date.from(expiryDate))
      .claim("userId", userPrincipal.getId())
      .claim("authorities", authorities)
      .claim("tokenType", "ACCESS")
      .signWith(secretKey)
      .compact();
  }

  public String generateRefreshToken(Authentication authentication) {
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

    Instant now = Instant.now();
    Instant expiryDate = now.plus(refreshTokenExpiration, ChronoUnit.MILLIS);

    return Jwts.builder()
      .subject(userPrincipal.getUsername())
      .issuedAt(Date.from(now))
      .expiration(Date.from(expiryDate))
      .claim("userId", userPrincipal.getId())
      .claim("tokenType", "REFRESH")
      .signWith(secretKey)
      .compact();
  }

  public String getUsernameFromToken(String token) {
    Claims claims = parseToken(token);
    return claims.getSubject();
  }

  public Long getUserIdFromToken(String token) {
    Claims claims = parseToken(token);
    return claims.get("userId", Long.class);
  }

  public String getTokenTypeFromToken(String token) {
    Claims claims = parseToken(token);
    return claims.get("tokenType", String.class);
  }

  public boolean isTokenValid(String token) {
    try {
      parseToken(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      log.debug("Invalid JWT token: {}", e.getMessage());
      return false;
    }
  }

  public boolean isTokenExpired(String token) {
    try {
      Claims claims = parseToken(token);
      return claims.getExpiration().before(new Date());
    } catch (JwtException | IllegalArgumentException e) {
      return true;
    }
  }

  private Claims parseToken(String token) {
    return Jwts.parser()
      .verifyWith(secretKey)
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }
}
