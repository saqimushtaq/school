package com.saqib.school.academic.mapper;

import com.saqib.school.academic.entity.SchoolClass;
import com.saqib.school.academic.model.SchoolClassRequest;
import com.saqib.school.academic.model.SchoolClassResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SchoolClassMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "session", ignore = true)
  @Mapping(target = "isActive", ignore = true)
  @Mapping(target = "classSubjects", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  SchoolClass toEntity(SchoolClassRequest request);

  @Mapping(target = "sessionId", source = "session.id")
  @Mapping(target = "sessionName", source = "session.sessionName")
  @Mapping(target = "displayName", source = "displayName")
  SchoolClassResponse toResponse(SchoolClass schoolClass);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "session", ignore = true)
  @Mapping(target = "isActive", ignore = true)
  @Mapping(target = "classSubjects", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateEntity(SchoolClassRequest request, @MappingTarget SchoolClass schoolClass);
}
