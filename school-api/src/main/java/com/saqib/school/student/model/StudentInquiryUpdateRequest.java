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
public class StudentInquiryUpdateRequest {

    @Size(min = 1, max = 100, message = "Student name must be between 1 and 100 characters")
    private String studentName;

    @Size(min = 1, max = 100, message = "Parent name must be between 1 and 100 characters")
    private String parentName;

    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]{10,20}$", message = "Invalid phone number format")
    private String parentPhone;

    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String parentEmail;

    @Size(max = 50, message = "Interested class must not exceed 50 characters")
    private String interestedClass;

    private StudentInquiry.InquirySource inquirySource;

    @Size(max = 500, message = "Referral details must not exceed 500 characters")
    private String referralDetails;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;

    private StudentInquiry.InquiryStatus status;

    private LocalDate followUpDate;

    private Boolean registrationFeePaid;
}
