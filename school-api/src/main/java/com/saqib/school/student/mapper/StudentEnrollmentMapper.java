package com.saqib.school.student.mapper;

import com.saqib.school.student.entity.StudentEnrollment;
import com.saqib.school.student.model.StudentEnrollmentRequest;
import com.saqib.school.student.model.StudentEnrollmentResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StudentEnrollmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "schoolClass", ignore = true)
    @Mapping(target = "completionDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    StudentEnrollment toEntity(StudentEnrollmentRequest request);

    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.fullName")
    @Mapping(target = "classId", source = "schoolClass.id")
    @Mapping(target = "className", source = "schoolClass.className")
    @Mapping(target = "classSection", source = "schoolClass.section")
    @Mapping(target = "sessionName", source = "schoolClass.session.sessionName")
    StudentEnrollmentResponse toResponse(StudentEnrollment enrollment);
}
