package com.saqib.school.student.mapper;

import com.saqib.school.student.entity.StudentGuardian;
import com.saqib.school.student.model.StudentGuardianRequest;
import com.saqib.school.student.model.StudentGuardianResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StudentGuardianMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    StudentGuardian toEntity(StudentGuardianRequest request);

    StudentGuardianResponse toResponse(StudentGuardian guardian);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(StudentGuardianRequest request, @MappingTarget StudentGuardian guardian);
}
