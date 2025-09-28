package com.saqib.school.student.model;

import com.saqib.school.student.entity.StudentInquiry;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentInquiryRequest {

    @NotNull(message = "Inquiry date is required")
    private LocalDate inquiryDate;

    @NotBlank(message = "Student name is required")
    @Size(max = 100, message = "Student name must not exceed 100 characters")
    private String studentName;

    @NotBlank(message = "Parent name is required")
    @Size(max = 100, message = "Parent name must not exceed 100 characters")
    private String parentName;

    @NotBlank(message = "Parent phone is required")
    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]{10,20}$", message = "Invalid phone number format")
    private String parentPhone;

    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String parentEmail;

    @Size(max = 50, message = "Interested class must not exceed 50 characters")
    private String interestedClass;

    @NotNull(message = "Inquiry source is required")
    private StudentInquiry.InquirySource inquirySource;

    @Size(max = 500, message = "Referral details must not exceed 500 characters")
    private String referralDetails;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;

    @Future(message = "Follow-up date must be in the future")
    private LocalDate followUpDate;

    @Builder.Default
    private Boolean registrationFeePaid = false;
}
