package com.saqib.school.security;

import com.saqib.school.common.audit.Auditable;
import com.saqib.school.common.exception.UnauthorizedException;
import com.saqib.school.user.entity.User;
import com.saqib.school.user.mapper.UserMapper;
import com.saqib.school.user.model.LoginRequest;
import com.saqib.school.user.model.LoginResponse;
import com.saqib.school.user.repository.UserRepository;
import com.saqib.school.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserService userService;
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Transactional
  @Auditable(action = "USER_LOGIN", entityType = "User")
  public LoginResponse login(LoginRequest request) {
    try {
      // Check if user exists and account is not locked
      User user = userRepository.findByUsername(request.getUsername())
        .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

      if (user.isAccountLocked()) {
        throw new UnauthorizedException("Account is temporarily locked. Please try again later.");
      }

      if (!user.isActive()) {
        throw new UnauthorizedException("Account is not active. Please contact administrator.");
      }

      // Authenticate user
      Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
      );

      // Generate tokens
      String accessToken = jwtService.generateAccessToken(authentication);
      String refreshToken = jwtService.generateRefreshToken(authentication);

      // Record successful login
      userService.recordSuccessfulLogin(request.getUsername());

      // Build response
      LoginResponse response = userMapper.toLoginResponse(user);
      response.setAccessToken(accessToken);
      response.setRefreshToken(refreshToken);
      response.setTokenType("Bearer");
      response.setExpiresIn(jwtService.getAccessTokenExpiration() / 1000); // Convert to seconds

      log.info("User logged in successfully: {}", request.getUsername());
      return response;

    } catch (BadCredentialsException e) {
      // Record failed login attempt
      userService.recordFailedLogin(request.getUsername());
      log.warn("Failed login attempt for username: {}", request.getUsername());
      throw new BadCredentialsException("Invalid username or password");

    } catch (AuthenticationException e) {
      log.warn("Authentication failed for username: {}", request.getUsername());
      throw new UnauthorizedException("Authentication failed");
    }
  }

  @Transactional(readOnly = true)
  @Auditable(action = "REFRESH_TOKEN", entityType = "User")
  public LoginResponse refreshToken(String refreshToken) {
    try {
      if (!jwtService.isTokenValid(refreshToken)) {
        throw new UnauthorizedException("Invalid refresh token");
      }

      String tokenType = jwtService.getTokenTypeFromToken(refreshToken);
      if (!"REFRESH".equals(tokenType)) {
        throw new UnauthorizedException("Invalid token type");
      }

      if (jwtService.isTokenExpired(refreshToken)) {
        throw new UnauthorizedException("Refresh token has expired");
      }

      String username = jwtService.getUsernameFromToken(refreshToken);
      User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UnauthorizedException("User not found"));

      if (!user.isActive()) {
        throw new UnauthorizedException("Account is not active");
      }

      // Create new authentication object for token generation
      UserPrincipal userPrincipal = UserPrincipal.create(user);
      Authentication authentication = new UsernamePasswordAuthenticationToken(
        userPrincipal, null, userPrincipal.getAuthorities()
      );

      // Generate new tokens
      String newAccessToken = jwtService.generateAccessToken(authentication);
      String newRefreshToken = jwtService.generateRefreshToken(authentication);

      // Build response
      LoginResponse response = userMapper.toLoginResponse(user);
      response.setAccessToken(newAccessToken);
      response.setRefreshToken(newRefreshToken);
      response.setTokenType("Bearer");
      response.setExpiresIn(jwtService.getAccessTokenExpiration() / 1000);

      log.info("Token refreshed successfully for user: {}", username);
      return response;

    } catch (Exception e) {
      log.warn("Token refresh failed: {}", e.getMessage());
      throw new UnauthorizedException("Invalid or expired refresh token");
    }
  }

  @Auditable(action = "USER_LOGOUT", entityType = "User")
  public void logout(String accessToken) {
    try {
      if (jwtService.isTokenValid(accessToken)) {
        String username = jwtService.getUsernameFromToken(accessToken);
        log.info("User logged out successfully: {}", username);
      }
      // In a production system, you would typically:
      // 1. Add the token to a blacklist/redis cache
      // 2. Or use shorter token expiration times
      // For now, we just log the logout event
    } catch (Exception e) {
      log.debug("Logout attempted with invalid token: {}", e.getMessage());
    }
  }

  @Transactional(readOnly = true)
  public boolean validateToken(String token) {
    return jwtService.isTokenValid(token) && !jwtService.isTokenExpired(token);
  }

  @Transactional(readOnly = true)
  public String getUsernameFromToken(String token) {
    if (!validateToken(token)) {
      throw new UnauthorizedException("Invalid or expired token");
    }
    return jwtService.getUsernameFromToken(token);
  }
}
