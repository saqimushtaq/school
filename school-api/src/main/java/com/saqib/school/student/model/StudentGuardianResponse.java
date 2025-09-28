package com.saqib.school.student.model;

import com.saqib.school.student.entity.StudentGuardian;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentGuardianResponse {
    private Long id;
    private StudentGuardian.GuardianType guardianType;
    private String name;
    private String cnic;
    private String phone;
    private String email;
    private String occupation;
    private String address;
    private Boolean isPrimaryContact;
    private LocalDateTime createdAt;
}
