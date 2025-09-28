package com.saqib.school.user.entity;

import com.saqib.school.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends BaseEntity {

  @Column(unique = true, nullable = false, length = 50)
  private String username;

  @Column(unique = true, length = 100)
  private String email;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Column(name = "first_name", nullable = false, length = 100)
  private String firstName;

  @Column(name = "last_name", nullable = false, length = 100)
  private String lastName;

  @Column(length = 20)
  private String phone;

  @Column(columnDefinition = "TEXT")
  private String address;

  @Column(name = "photo_url")
  private String photoUrl;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  @Builder.Default
  private UserStatus status = UserStatus.ACTIVE;

  @Column(name = "last_login_at")
  private LocalDateTime lastLoginAt;

  @Column(name = "password_changed_at")
  @Builder.Default
  private LocalDateTime passwordChangedAt = LocalDateTime.now();

  @Column(name = "failed_login_attempts")
  @Builder.Default
  private Integer failedLoginAttempts = 0;

  @Column(name = "account_locked_until")
  private LocalDateTime accountLockedUntil;

  @Column(name = "must_change_password")
  @Builder.Default
  private Boolean mustChangePassword = false;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<UserRole> userRoles;

  // Helper methods
  public boolean isAccountLocked() {
    return accountLockedUntil != null && accountLockedUntil.isAfter(LocalDateTime.now());
  }

  public boolean isActive() {
    return UserStatus.ACTIVE.equals(status) && !isAccountLocked();
  }

  public enum UserStatus {
    ACTIVE, INACTIVE, SUSPENDED, LOCKED
  }
}
