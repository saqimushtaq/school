package com.saqib.school.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
  private String accessToken;
  private String refreshToken;
  private String tokenType;
  private Long expiresIn;
  private UserInfo user;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class UserInfo {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Set<String> roles;
    private Boolean mustChangePassword;
    private LocalDateTime lastLoginAt;
  }
}
