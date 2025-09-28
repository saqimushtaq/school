package com.saqib.school.student.mapper;

import com.saqib.school.student.entity.Student;
import com.saqib.school.student.entity.StudentEnrollment;
import com.saqib.school.student.model.StudentEnrollmentResponse;
import com.saqib.school.student.model.StudentRequest;
import com.saqib.school.student.model.StudentResponse;
import com.saqib.school.student.model.StudentUpdateRequest;
import org.mapstruct.*;


import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring",
        uses = {StudentGuardianMapper.class, StudentEnrollmentMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StudentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registrationNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "guardians", ignore = true)
    @Mapping(target = "enrollments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Student toEntity(StudentRequest request);

    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "currentEnrollment", expression = "java(mapCurrentEnrollment(student.getEnrollments()))")
    StudentResponse toResponse(Student student);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registrationNumber", ignore = true)
    @Mapping(target = "dateOfBirth", ignore = true)
    @Mapping(target = "gender", ignore = true)
    @Mapping(target = "admissionDate", ignore = true)
    @Mapping(target = "guardians", ignore = true)
    @Mapping(target = "enrollments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(StudentUpdateRequest request, @MappingTarget Student student);

    default StudentEnrollmentResponse mapCurrentEnrollment(Set<StudentEnrollment> enrollments) {
        if (enrollments == null || enrollments.isEmpty()) {
            return null;
        }

        StudentEnrollment currentEnrollment = enrollments.stream()
            .filter(enrollment -> enrollment.getStatus() == StudentEnrollment.EnrollmentStatus.ACTIVE)
            .findFirst()
            .orElse(null);

        if (currentEnrollment == null) {
            return null;
        }

        return StudentEnrollmentResponse.builder()
            .id(currentEnrollment.getId())
            .studentId(currentEnrollment.getStudent().getId())
            .classId(currentEnrollment.getSchoolClass().getId())
            .className(currentEnrollment.getSchoolClass().getClassName())
            .classSection(currentEnrollment.getSchoolClass().getSection())
            .sessionName(currentEnrollment.getSchoolClass().getSession().getSessionName())
            .enrollmentDate(currentEnrollment.getEnrollmentDate())
            .completionDate(currentEnrollment.getCompletionDate())
            .status(currentEnrollment.getStatus())
            .createdAt(currentEnrollment.getCreatedAt())
            .build();
    }
}
