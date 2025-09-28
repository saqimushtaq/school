package com.saqib.school.academic.mapper;

import com.saqib.school.academic.entity.AcademicSession;
import com.saqib.school.academic.model.AcademicSessionRequest;
import com.saqib.school.academic.model.AcademicSessionResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AcademicSessionMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "classes", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  AcademicSession toEntity(AcademicSessionRequest request);

  AcademicSessionResponse toResponse(AcademicSession session);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "classes", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateEntity(AcademicSessionRequest request, @MappingTarget AcademicSession session);
}
