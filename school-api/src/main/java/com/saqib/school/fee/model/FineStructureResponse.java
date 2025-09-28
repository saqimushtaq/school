package com.saqib.school.fee.model;

import com.saqib.school.fee.entity.FineStructure;
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
public class FineStructureResponse {
    private Long id;
    private Long classId;
    private String className;
    private String classSection;
    private Integer daysAfterDue;
    private FineStructure.FineType fineType;
    private BigDecimal fineValue;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
