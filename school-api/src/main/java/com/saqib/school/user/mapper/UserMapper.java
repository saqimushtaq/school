package com.saqib.school.user.mapper;

import com.saqib.school.user.entity.User;
import com.saqib.school.user.entity.UserRole;
import com.saqib.school.user.model.LoginResponse;
import com.saqib.school.user.model.UserRequest;
import com.saqib.school.user.model.UserResponse;
import com.saqib.school.user.model.UserUpdateRequest;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "passwordHash", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "lastLoginAt", ignore = true)
  @Mapping(target = "passwordChangedAt", ignore = true)
  @Mapping(target = "failedLoginAttempts", ignore = true)
  @Mapping(target = "accountLockedUntil", ignore = true)
  @Mapping(target = "mustChangePassword", ignore = true)
  @Mapping(target = "userRoles", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  User toEntity(UserRequest request);

  @Mapping(target = "roles", source = "userRoles", qualifiedByName = "mapRoles")
  UserResponse toResponse(User user);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "username", ignore = true)
  @Mapping(target = "passwordHash", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "lastLoginAt", ignore = true)
  @Mapping(target = "passwordChangedAt", ignore = true)
  @Mapping(target = "failedLoginAttempts", ignore = true)
  @Mapping(target = "accountLockedUntil", ignore = true)
  @Mapping(target = "mustChangePassword", ignore = true)
  @Mapping(target = "userRoles", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateEntity(UserUpdateRequest request, @MappingTarget User user);

  @Mapping(target = "user.id", source = "id")
  @Mapping(target = "user.username", source = "username")
  @Mapping(target = "user.firstName", source = "firstName")
  @Mapping(target = "user.lastName", source = "lastName")
  @Mapping(target = "user.email", source = "email")
  @Mapping(target = "user.roles", source = "userRoles", qualifiedByName = "mapRoles")
  @Mapping(target = "user.mustChangePassword", source = "mustChangePassword")
  @Mapping(target = "user.lastLoginAt", source = "lastLoginAt")
  @Mapping(target = "accessToken", ignore = true)
  @Mapping(target = "refreshToken", ignore = true)
  @Mapping(target = "tokenType", ignore = true)
  @Mapping(target = "expiresIn", ignore = true)
  LoginResponse toLoginResponse(User user);

  @Named("mapRoles")
  default Set<String> mapRoles(Set<UserRole> userRoles) {
    if (userRoles == null) {
      return Set.of();
    }
    return userRoles.stream()
      .map(userRole -> userRole.getRole().getRoleName())
      .collect(Collectors.toSet());
  }
}
