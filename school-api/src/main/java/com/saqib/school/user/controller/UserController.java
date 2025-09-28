package com.saqib.school.user.controller;

import com.saqib.school.common.dto.ApiResponse;
import com.saqib.school.common.dto.PageResponse;
import com.saqib.school.user.entity.User;
import com.saqib.school.user.model.UserRequest;
import com.saqib.school.user.model.UserResponse;
import com.saqib.school.user.model.UserUpdateRequest;
import com.saqib.school.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User CRUD operations and management")
public class UserController {

  private final UserService userService;

  @PostMapping
  @Operation(summary = "Create user", description = "Create a new user account")
  public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserRequest request) {
    UserResponse response = userService.createUser(request);
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(ApiResponse.success("User created successfully", response));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get user by ID", description = "Retrieve user details by ID")
  public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
    UserResponse response = userService.getUserById(id);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/username/{username}")
  @Operation(summary = "Get user by username", description = "Retrieve user details by username")
  public ResponseEntity<ApiResponse<UserResponse>> getUserByUsername(@PathVariable String username) {
    UserResponse response = userService.getUserByUsername(username);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping
  @Operation(summary = "Get all users", description = "Retrieve paginated list of all users")
  public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(defaultValue = "id") String sortBy,
    @RequestParam(defaultValue = "asc") String sortDir) {

    Sort sort = sortDir.equalsIgnoreCase("desc") ?
      Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
    Pageable pageable = PageRequest.of(page, size, sort);

    PageResponse<UserResponse> response = userService.getAllUsers(pageable);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/status/{status}")
  @Operation(summary = "Get users by status", description = "Retrieve users filtered by status")
  public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getUsersByStatus(
    @PathVariable User.UserStatus status,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    PageResponse<UserResponse> response = userService.getUsersByStatus(status, pageable);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/role/{roleName}")
  @Operation(summary = "Get users by role", description = "Retrieve users filtered by role")
  public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getUsersByRole(
    @PathVariable String roleName,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    PageResponse<UserResponse> response = userService.getUsersByRole(roleName, pageable);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update user", description = "Update user information")
  public ResponseEntity<ApiResponse<UserResponse>> updateUser(
    @PathVariable Long id,
    @Valid @RequestBody UserUpdateRequest request) {

    UserResponse response = userService.updateUser(id, request);
    return ResponseEntity.ok(ApiResponse.success("User updated successfully", response));
  }

  @PostMapping("/{id}/roles/{roleName}")
  @Operation(summary = "Assign role to user", description = "Assign a role to specified user")
  public ResponseEntity<ApiResponse<String>> assignRole(
    @PathVariable Long id,
    @PathVariable String roleName) {

    userService.assignRoleToUser(id, roleName);
    return ResponseEntity.ok(ApiResponse.success("Role assigned successfully"));
  }

  @DeleteMapping("/{id}/roles/{roleName}")
  @Operation(summary = "Remove role from user", description = "Remove a role from specified user")
  public ResponseEntity<ApiResponse<String>> removeRole(
    @PathVariable Long id,
    @PathVariable String roleName) {

    userService.removeRoleFromUser(id, roleName);
    return ResponseEntity.ok(ApiResponse.success("Role removed successfully"));
  }

  @PutMapping("/{id}/status")
  @Operation(summary = "Update user status", description = "Update user account status")
  public ResponseEntity<ApiResponse<String>> updateUserStatus(
    @PathVariable Long id,
    @RequestParam User.UserStatus status) {

    userService.updateUserStatus(id, status);
    return ResponseEntity.ok(ApiResponse.success("User status updated successfully"));
  }

  @GetMapping("/{id}/roles")
  @Operation(summary = "Get user roles", description = "Retrieve roles assigned to user")
  public ResponseEntity<ApiResponse<List<String>>> getUserRoles(@PathVariable Long id) {
    List<String> roles = userService.getUserRoles(id);
    return ResponseEntity.ok(ApiResponse.success(roles));
  }
}
