package com.saqib.school.user.mapper;

import com.saqib.school.user.entity.Role;
import com.saqib.school.user.model.RoleRequest;
import com.saqib.school.user.model.RoleResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RoleMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "isActive", ignore = true)
  @Mapping(target = "userRoles", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Role toEntity(RoleRequest request);

  RoleResponse toResponse(Role role);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "isActive", ignore = true)
  @Mapping(target = "userRoles", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateEntity(RoleRequest request, @MappingTarget Role role);
}
