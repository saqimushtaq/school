package com.saqib.school.user.model;

import com.saqib.school.user.entity.User;
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
public class UserResponse {
  private Long id;
  private String username;
  private String email;
  private String firstName;
  private String lastName;
  private String phone;
  private String address;
  private String photoUrl;
  private User.UserStatus status;
  private LocalDateTime lastLoginAt;
  private Boolean mustChangePassword;
  private Set<String> roles;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
