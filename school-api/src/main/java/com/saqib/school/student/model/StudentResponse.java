package com.saqib.school.student.model;

import com.saqib.school.student.entity.Student;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {
    private Long id;
    private String registrationNumber;
    private String firstName;
    private String lastName;
    private String fullName;
    private LocalDate dateOfBirth;
    private Student.Gender gender;
    private String bloodGroup;
    private String address;
    private String phone;
    private String email;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String medicalConditions;
    private String photoUrl;
    private LocalDate admissionDate;
    private Student.StudentStatus status;
    private List<StudentGuardianResponse> guardians;
    private StudentEnrollmentResponse currentEnrollment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
