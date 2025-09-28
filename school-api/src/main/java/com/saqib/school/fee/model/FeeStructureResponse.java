package com.saqib.school.fee.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeStructureResponse {
    private Long id;
    private Long classId;
    private String className;
    private String classSection;
    private Long feeCategoryId;
    private String feeCategoryName;
    private BigDecimal amount;
    private Boolean isMonthly;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
