package com.saqib.school.academic.mapper;

import com.saqib.school.academic.entity.Subject;
import com.saqib.school.academic.model.SubjectRequest;
import com.saqib.school.academic.model.SubjectResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SubjectMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "isActive", ignore = true)
  @Mapping(target = "classSubjects", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Subject toEntity(SubjectRequest request);

  SubjectResponse toResponse(Subject subject);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "isActive", ignore = true)
  @Mapping(target = "classSubjects", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateEntity(SubjectRequest request, @MappingTarget Subject subject);
}
