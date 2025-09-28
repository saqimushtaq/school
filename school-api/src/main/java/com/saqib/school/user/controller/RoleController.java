package com.saqib.school.user.controller;

import com.saqib.school.common.dto.ApiResponse;
import com.saqib.school.common.dto.PageResponse;
import com.saqib.school.user.model.RoleRequest;
import com.saqib.school.user.model.RoleResponse;
import com.saqib.school.user.service.RoleService;
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
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Role Management", description = "Role CRUD operations and management")
public class RoleController {

  private final RoleService roleService;

  @PostMapping
  @Operation(summary = "Create role", description = "Create a new role")
  public ResponseEntity<ApiResponse<RoleResponse>> createRole(@Valid @RequestBody RoleRequest request) {
    RoleResponse response = roleService.createRole(request);
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(ApiResponse.success("Role created successfully", response));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get role by ID", description = "Retrieve role details by ID")
  public ResponseEntity<ApiResponse<RoleResponse>> getRoleById(@PathVariable Long id) {
    RoleResponse response = roleService.getRoleById(id);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/name/{roleName}")
  @Operation(summary = "Get role by name", description = "Retrieve role details by name")
  public ResponseEntity<ApiResponse<RoleResponse>> getRoleByName(@PathVariable String roleName) {
    RoleResponse response = roleService.getRoleByName(roleName);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping
  @Operation(summary = "Get all roles", description = "Retrieve paginated list of all roles")
  public ResponseEntity<ApiResponse<PageResponse<RoleResponse>>> getAllRoles(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(defaultValue = "roleName") String sortBy,
    @RequestParam(defaultValue = "asc") String sortDir) {

    Sort sort = sortDir.equalsIgnoreCase("desc") ?
      Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
    Pageable pageable = PageRequest.of(page, size, sort);

    PageResponse<RoleResponse> response = roleService.getAllRoles(pageable);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/active")
  @Operation(summary = "Get active roles", description = "Retrieve list of active roles")
  public ResponseEntity<ApiResponse<List<RoleResponse>>> getActiveRoles() {
    List<RoleResponse> response = roleService.getActiveRoles();
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update role", description = "Update role information")
  public ResponseEntity<ApiResponse<RoleResponse>> updateRole(
    @PathVariable Long id,
    @Valid @RequestBody RoleRequest request) {

    RoleResponse response = roleService.updateRole(id, request);
    return ResponseEntity.ok(ApiResponse.success("Role updated successfully", response));
  }

  @PutMapping("/{id}/activate")
  @Operation(summary = "Activate role", description = "Activate a role")
  public ResponseEntity<ApiResponse<String>> activateRole(@PathVariable Long id) {
    roleService.activateRole(id);
    return ResponseEntity.ok(ApiResponse.success("Role activated successfully"));
  }

  @PutMapping("/{id}/deactivate")
  @Operation(summary = "Deactivate role", description = "Deactivate a role")
  public ResponseEntity<ApiResponse<String>> deactivateRole(@PathVariable Long id) {
    roleService.deactivateRole(id);
    return ResponseEntity.ok(ApiResponse.success("Role deactivated successfully"));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete role", description = "Delete a role")
  public ResponseEntity<ApiResponse<String>> deleteRole(@PathVariable Long id) {
    roleService.deleteRole(id);
    return ResponseEntity.ok(ApiResponse.success("Role deleted successfully"));
  }
}
