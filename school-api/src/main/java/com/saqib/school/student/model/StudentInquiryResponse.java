package com.saqib.school.student.model;

import com.saqib.school.student.entity.StudentInquiry;
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
public class StudentInquiryResponse {
    private Long id;
    private LocalDate inquiryDate;
    private String studentName;
    private String parentName;
    private String parentPhone;
    private String parentEmail;
    private String interestedClass;
    private StudentInquiry.InquirySource inquirySource;
    private String referralDetails;
    private String notes;
    private StudentInquiry.InquiryStatus status;
    private LocalDate followUpDate;
    private Boolean registrationFeePaid;
    private String createdByUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
