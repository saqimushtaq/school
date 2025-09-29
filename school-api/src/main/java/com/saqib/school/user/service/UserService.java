package com.saqib.school.user.service;

import com.saqib.school.common.audit.Auditable;
import com.saqib.school.common.dto.PageResponse;
import com.saqib.school.common.exception.BadRequestException;
import com.saqib.school.common.exception.ResourceNotFoundException;
import com.saqib.school.user.entity.Role;
import com.saqib.school.user.entity.User;
import com.saqib.school.user.entity.UserRole;
import com.saqib.school.user.mapper.UserMapper;
import com.saqib.school.user.model.ChangePasswordRequest;
import com.saqib.school.user.model.UserRequest;
import com.saqib.school.user.model.UserResponse;
import com.saqib.school.user.model.UserUpdateRequest;
import com.saqib.school.user.repository.RoleRepository;
import com.saqib.school.user.repository.UserRepository;
import com.saqib.school.user.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final UserRoleRepository userRoleRepository;
  private final UserMapper userMapper;
  private final PasswordService passwordService;

  @Value("${app.security.login.max-failed-attempts:5}")
  private int maxFailedAttempts;

  @Value("${app.security.login.lockout-duration:300}")
  private int lockoutDurationSeconds;

  @Transactional
  @Auditable(action = "CREATE_USER", entityType = "User")
  public UserResponse createUser(UserRequest request) {
    validateUniqueConstraints(request.getUsername(), request.getEmail(), null);

    User user = userMapper.toEntity(request);
    user.setPasswordHash(passwordService.encodePassword(request.getPassword()));
    user.setStatus(User.UserStatus.ACTIVE);
    user.setMustChangePassword(false);

    User savedUser = userRepository.save(user);
    log.info("User created successfully: {}", savedUser.getUsername());

    return userMapper.toResponse(savedUser);
  }

  @Transactional(readOnly = true)
  public UserResponse getUserById(Long id) {
    User user = findUserById(id);
    return userMapper.toResponse(user);
  }

  @Transactional(readOnly = true)
  public UserResponse getUserByUsername(String username) {
    User user = userRepository.findByUsername(username)
      .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    return userMapper.toResponse(user);
  }

  @Transactional(readOnly = true)
  public PageResponse<UserResponse> getAllUsers(Pageable pageable) {
    var page = userRepository.findAll(pageable)
      .map(userMapper::toResponse);

    return PageResponse.from(page);
  }

  @Transactional(readOnly = true)
  public PageResponse<UserResponse> getUsersByStatus(User.UserStatus status, Pageable pageable) {
    var userPage = userRepository.findByStatus(status, pageable).map(userMapper::toResponse);
    return PageResponse.from(userPage);
  }

  @Transactional(readOnly = true)
  public PageResponse<UserResponse> getUsersByRole(String roleName, Pageable pageable) {
    var userPage = userRepository.findByRoleName(roleName, pageable).map(userMapper::toResponse);
    return PageResponse.from(userPage);
  }

  @Transactional
  @Auditable(action = "UPDATE_USER", entityType = "User")
  public UserResponse updateUser(Long id, UserUpdateRequest request) {
    User user = findUserById(id);
    validateUniqueConstraints(null, request.getEmail(), id);

    userMapper.updateEntity(request, user);
    User updatedUser = userRepository.save(user);

    log.info("User updated successfully: {}", updatedUser.getUsername());
    return userMapper.toResponse(updatedUser);
  }

  @Transactional
  @Auditable(action = "CHANGE_PASSWORD", entityType = "User")
  public void changePassword(Long userId, ChangePasswordRequest request) {
    if (!request.getNewPassword().equals(request.getConfirmPassword())) {
      throw new BadRequestException("New password and confirmation do not match");
    }

    User user = findUserById(userId);

    if (!passwordService.matchesPassword(request.getCurrentPassword(), user.getPasswordHash())) {
      throw new BadRequestException("Current password is incorrect");
    }

    user.setPasswordHash(passwordService.encodePassword(request.getNewPassword()));
    user.setPasswordChangedAt(LocalDateTime.now());
    user.setMustChangePassword(false);

    userRepository.save(user);
    log.info("Password changed successfully for user: {}", user.getUsername());
  }

  @Transactional
  @Auditable(action = "RESET_PASSWORD", entityType = "User")
  public void resetPassword(Long userId, String newPassword) {
    User user = findUserById(userId);

    user.setPasswordHash(passwordService.encodePassword(newPassword));
    user.setPasswordChangedAt(LocalDateTime.now());
    user.setMustChangePassword(true);
    user.setFailedLoginAttempts(0);
    user.setAccountLockedUntil(null);

    userRepository.save(user);
    log.info("Password reset successfully for user: {}", user.getUsername());
  }

  @Transactional
  @Auditable(action = "ASSIGN_ROLE", entityType = "User")
  public void assignRoleToUser(Long userId, String roleName) {
    User user = findUserById(userId);
    Role role = findRoleByName(roleName);

    boolean alreadyAssigned = userRoleRepository.findByUserIdAndRoleId(userId, role.getId()).isPresent();
    if (alreadyAssigned) {
      throw new BadRequestException("Role " + roleName + " is already assigned to user");
    }

    UserRole userRole = UserRole.builder()
      .user(user)
      .role(role)
      .assignedBy(getCurrentUser())
      .build();

    userRoleRepository.save(userRole);
    log.info("Role {} assigned to user: {}", roleName, user.getUsername());
  }

  @Transactional
  @Auditable(action = "REMOVE_ROLE", entityType = "User")
  public void removeRoleFromUser(Long userId, String roleName) {
    User user = findUserById(userId);
    Role role = findRoleByName(roleName);

    UserRole userRole = userRoleRepository.findByUserIdAndRoleId(userId, role.getId())
      .orElseThrow(() -> new BadRequestException("Role " + roleName + " is not assigned to user"));

    userRoleRepository.delete(userRole);
    log.info("Role {} removed from user: {}", roleName, user.getUsername());
  }

  @Transactional
  @Auditable(action = "UPDATE_USER_STATUS", entityType = "User")
  public void updateUserStatus(Long userId, User.UserStatus status) {
    User user = findUserById(userId);
    user.setStatus(status);

    if (status == User.UserStatus.ACTIVE) {
      user.setFailedLoginAttempts(0);
      user.setAccountLockedUntil(null);
    }

    userRepository.save(user);
    log.info("User status updated to {} for user: {}", status, user.getUsername());
  }

  @Transactional
  public void recordFailedLogin(String username) {
    userRepository.findByUsername(username).ifPresent(user -> {
      int newFailedAttempts = user.getFailedLoginAttempts() + 1;
      user.setFailedLoginAttempts(newFailedAttempts);

      if (newFailedAttempts >= maxFailedAttempts) {
        LocalDateTime lockUntil = LocalDateTime.now().plusSeconds(lockoutDurationSeconds);
        user.setAccountLockedUntil(lockUntil);
        log.warn("Account locked for user: {} due to {} failed login attempts", username, newFailedAttempts);
      }

      userRepository.save(user);
    });
  }

  @Transactional
  public void recordSuccessfulLogin(String username) {
    userRepository.findByUsername(username).ifPresent(user -> {
      user.setFailedLoginAttempts(0);
      user.setAccountLockedUntil(null);
      user.setLastLoginAt(LocalDateTime.now());
      userRepository.save(user);
    });
  }

  @Transactional(readOnly = true)
  public List<String> getUserRoles(Long userId) {
    return userRoleRepository.findByUserId(userId)
      .stream()
      .map(userRole -> userRole.getRole().getRoleName())
      .collect(Collectors.toList());
  }

  private User findUserById(Long id) {
    return userRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
  }

  private Role findRoleByName(String roleName) {
    return roleRepository.findByRoleName(roleName)
      .orElseThrow(() -> new ResourceNotFoundException("Role", "roleName", roleName));
  }

  private void validateUniqueConstraints(String username, String email, Long excludeUserId) {
    if (username != null) {
      boolean usernameExists = excludeUserId != null ?
        userRepository.findByUsername(username)
          .map(User::getId)
          .filter(id -> !id.equals(excludeUserId))
          .isPresent() :
        userRepository.existsByUsername(username);

      if (usernameExists) {
        throw new BadRequestException("Username is already taken");
      }
    }

    if (email != null) {
      boolean emailExists = excludeUserId != null ?
        userRepository.findByEmail(email)
          .map(User::getId)
          .filter(id -> !id.equals(excludeUserId))
          .isPresent() :
        userRepository.existsByEmail(email);

      if (emailExists) {
        throw new BadRequestException("Email is already taken");
      }
    }
  }

  public User getCurrentUser() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return userRepository.findByUsername(username).orElse(null);
  }
}
