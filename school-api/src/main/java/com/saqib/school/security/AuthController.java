package com.saqib.school.security;

import com.saqib.school.common.dto.ApiResponse;
import com.saqib.school.user.model.ChangePasswordRequest;
import com.saqib.school.user.model.LoginRequest;
import com.saqib.school.user.model.LoginResponse;
import com.saqib.school.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and authorization")
public class AuthController {

  private final AuthenticationService authenticationService;
  private final UserService userService;

  @PostMapping("/login")
  @Operation(summary = "User login", description = "Authenticate user and return JWT tokens")
  public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
    LoginResponse response = authenticationService.login(request);
    return ResponseEntity.ok(ApiResponse.success("Login successful", response));
  }

  @PostMapping("/refresh")
  @Operation(summary = "Refresh token", description = "Generate new access token using refresh token")
  public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@RequestHeader("Authorization") String refreshToken) {
    String token = extractToken(refreshToken);
    LoginResponse response = authenticationService.refreshToken(token);
    return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
  }

  @PostMapping("/logout")
  @Operation(summary = "User logout", description = "Logout user and invalidate token")
  public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null) {
      String token = extractToken(authHeader);
      authenticationService.logout(token);
    }
    return ResponseEntity.ok(ApiResponse.success("Logout successful"));
  }

  @PostMapping("/change-password")
  @Operation(summary = "Change password", description = "Change current user's password")
  public ResponseEntity<ApiResponse<String>> changePassword(
    @Valid @RequestBody ChangePasswordRequest request,
    Authentication authentication) {

    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    userService.changePassword(userPrincipal.getId(), request);

    return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
  }

  @PostMapping("/reset-password/{userId}")
  @Operation(summary = "Reset user password", description = "Reset password for specified user (Admin only)")
  public ResponseEntity<ApiResponse<String>> resetPassword(
    @PathVariable Long userId,
    @RequestParam String newPassword) {

    userService.resetPassword(userId, newPassword);
    return ResponseEntity.ok(ApiResponse.success("Password reset successfully"));
  }

  @GetMapping("/validate")
  @Operation(summary = "Validate token", description = "Validate JWT token")
  public ResponseEntity<ApiResponse<Boolean>> validateToken(@RequestHeader("Authorization") String authHeader) {
    String token = extractToken(authHeader);
    boolean isValid = authenticationService.validateToken(token);
    return ResponseEntity.ok(ApiResponse.success("Token validation result", isValid));
  }

  private String extractToken(String authHeader) {
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    }
    throw new IllegalArgumentException("Invalid authorization header format");
  }
}
