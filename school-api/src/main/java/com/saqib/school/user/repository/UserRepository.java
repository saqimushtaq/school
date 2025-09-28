package com.saqib.school.user.repository;

import com.saqib.school.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  @Query("SELECT u FROM User u WHERE u.status = :status")
  Page<User> findByStatus(@Param("status") User.UserStatus status, Pageable pageable);

  @Modifying
  @Query("UPDATE User u SET u.failedLoginAttempts = :attempts WHERE u.id = :userId")
  void updateFailedLoginAttempts(@Param("userId") Long userId, @Param("attempts") int attempts);

  @Modifying
  @Query("UPDATE User u SET u.accountLockedUntil = :lockUntil WHERE u.id = :userId")
  void lockAccount(@Param("userId") Long userId, @Param("lockUntil") LocalDateTime lockUntil);

  @Modifying
  @Query("UPDATE User u SET u.lastLoginAt = :loginTime WHERE u.id = :userId")
  void updateLastLogin(@Param("userId") Long userId, @Param("loginTime") LocalDateTime loginTime);

  @Query("SELECT u FROM User u JOIN u.userRoles ur JOIN ur.role r WHERE r.roleName = :roleName AND u.status = 'ACTIVE'")
  Page<User> findByRoleName(@Param("roleName") String roleName, Pageable pageable);
}
