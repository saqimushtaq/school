package com.saqib.school.academic.mapper;

import com.saqib.school.academic.entity.ClassSubject;
import com.saqib.school.academic.model.ClassSubjectRequest;
import com.saqib.school.academic.model.ClassSubjectResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ClassSubjectMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "schoolClass", ignore = true)
  @Mapping(target = "subject", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  ClassSubject toEntity(ClassSubjectRequest request);

  @Mapping(target = "classId", source = "schoolClass.id")
  @Mapping(target = "className", source = "schoolClass.className")
  @Mapping(target = "section", source = "schoolClass.section")
  @Mapping(target = "subjectId", source = "subject.id")
  @Mapping(target = "subjectName", source = "subject.subjectName")
  @Mapping(target = "subjectCode", source = "subject.subjectCode")
  ClassSubjectResponse toResponse(ClassSubject classSubject);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "schoolClass", ignore = true)
  @Mapping(target = "subject", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateEntity(ClassSubjectRequest request, @MappingTarget ClassSubject classSubject);
}
