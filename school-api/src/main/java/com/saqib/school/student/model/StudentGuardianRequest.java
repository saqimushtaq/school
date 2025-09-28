package com.saqib.school.student.model;

import com.saqib.school.student.entity.StudentGuardian;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentGuardianRequest {

    @NotNull(message = "Guardian type is required")
    private StudentGuardian.GuardianType guardianType;

    @NotBlank(message = "Guardian name is required")
    @Size(max = 100, message = "Guardian name must not exceed 100 characters")
    private String name;

    @Pattern(regexp = "^[0-9]{5}-[0-9]{7}-[0-9]$", message = "CNIC format should be XXXXX-XXXXXXX-X")
    private String cnic;

    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]{0,20}$", message = "Invalid phone number format")
    private String phone;

    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Size(max = 100, message = "Occupation must not exceed 100 characters")
    private String occupation;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @Builder.Default
    private Boolean isPrimaryContact = false;
}
