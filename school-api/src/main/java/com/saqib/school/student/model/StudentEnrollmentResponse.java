package com.saqib.school.student.model;

import com.saqib.school.student.entity.StudentEnrollment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentEnrollmentResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long classId;
    private String className;
    private String classSection;
    private String sessionName;
    private LocalDate enrollmentDate;
    private LocalDate completionDate;
    private StudentEnrollment.EnrollmentStatus status;
    private LocalDateTime createdAt;
}
