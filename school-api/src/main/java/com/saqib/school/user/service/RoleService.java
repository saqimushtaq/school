package com.saqib.school.user.service;

import com.saqib.school.common.audit.Auditable;
import com.saqib.school.common.dto.PageResponse;
import com.saqib.school.common.exception.BadRequestException;
import com.saqib.school.common.exception.ResourceNotFoundException;
import com.saqib.school.user.entity.Role;
import com.saqib.school.user.mapper.RoleMapper;
import com.saqib.school.user.model.RoleRequest;
import com.saqib.school.user.model.RoleResponse;
import com.saqib.school.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {

  private final RoleRepository roleRepository;
  private final RoleMapper roleMapper;

  @Transactional
  @Auditable(action = "CREATE_ROLE", entityType = "Role")
  public RoleResponse createRole(RoleRequest request) {
    if (roleRepository.existsByRoleName(request.getRoleName())) {
      throw new BadRequestException("Role with name '" + request.getRoleName() + "' already exists");
    }

    Role role = roleMapper.toEntity(request);
    role.setIsActive(true);

    Role savedRole = roleRepository.save(role);
    log.info("Role created successfully: {}", savedRole.getRoleName());

    return roleMapper.toResponse(savedRole);
  }

  @Transactional(readOnly = true)
  public RoleResponse getRoleById(Long id) {
    Role role = findRoleById(id);
    return roleMapper.toResponse(role);
  }

  @Transactional(readOnly = true)
  public RoleResponse getRoleByName(String roleName) {
    Role role = roleRepository.findByRoleName(roleName)
      .orElseThrow(() -> new ResourceNotFoundException("Role", "roleName", roleName));
    return roleMapper.toResponse(role);
  }

  @Transactional(readOnly = true)
  public PageResponse<RoleResponse> getAllRoles(Pageable pageable) {
    Page<Role> rolePage = roleRepository.findAll(pageable);
    return buildPageResponse(rolePage);
  }

  @Transactional(readOnly = true)
  public List<RoleResponse> getActiveRoles() {
    return roleRepository.findAll()
      .stream()
      .filter(Role::getIsActive)
      .map(roleMapper::toResponse)
      .collect(Collectors.toList());
  }

  @Transactional
  @Auditable(action = "UPDATE_ROLE", entityType = "Role")
  public RoleResponse updateRole(Long id, RoleRequest request) {
    Role role = findRoleById(id);

    // Check if role name is being changed and if new name already exists
    if (!role.getRoleName().equals(request.getRoleName()) &&
      roleRepository.existsByRoleName(request.getRoleName())) {
      throw new BadRequestException("Role with name '" + request.getRoleName() + "' already exists");
    }

    roleMapper.updateEntity(request, role);
    Role updatedRole = roleRepository.save(role);

    log.info("Role updated successfully: {}", updatedRole.getRoleName());
    return roleMapper.toResponse(updatedRole);
  }

  @Transactional
  @Auditable(action = "ACTIVATE_ROLE", entityType = "Role")
  public void activateRole(Long id) {
    Role role = findRoleById(id);
    role.setIsActive(true);
    roleRepository.save(role);

    log.info("Role activated: {}", role.getRoleName());
  }

  @Transactional
  @Auditable(action = "DEACTIVATE_ROLE", entityType = "Role")
  public void deactivateRole(Long id) {
    Role role = findRoleById(id);
    role.setIsActive(false);
    roleRepository.save(role);

    log.info("Role deactivated: {}", role.getRoleName());
  }

  @Transactional
  @Auditable(action = "DELETE_ROLE", entityType = "Role")
  public void deleteRole(Long id) {
    Role role = findRoleById(id);

    // Check if role is assigned to any users
    if (!role.getUserRoles().isEmpty()) {
      throw new BadRequestException("Cannot delete role that is assigned to users");
    }

    roleRepository.delete(role);
    log.info("Role deleted successfully: {}", role.getRoleName());
  }

  private Role findRoleById(Long id) {
    return roleRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
  }

  private PageResponse<RoleResponse> buildPageResponse(Page<Role> rolePage) {
    List<RoleResponse> roles = rolePage.getContent()
      .stream()
      .map(roleMapper::toResponse)
      .collect(Collectors.toList());

    return PageResponse.<RoleResponse>builder()
      .content(roles)
      .page(rolePage.getNumber())
      .size(rolePage.getSize())
      .totalElements(rolePage.getTotalElements())
      .totalPages(rolePage.getTotalPages())
      .first(rolePage.isFirst())
      .last(rolePage.isLast())
      .hasNext(rolePage.hasNext())
      .hasPrevious(rolePage.hasPrevious())
      .build();
  }
}
