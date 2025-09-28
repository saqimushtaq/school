package com.saqib.school.fee.model;

import com.saqib.school.fee.entity.StudentDiscount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDiscountResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentRegistrationNumber;
    private Long feeCategoryId;
    private String feeCategoryName;
    private StudentDiscount.DiscountType discountType;
    private BigDecimal discountValue;
    private String reason;
    private LocalDate validFrom;
    private LocalDate validTo;
    private Boolean isActive;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
