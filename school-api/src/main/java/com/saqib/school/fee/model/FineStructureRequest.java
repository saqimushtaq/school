package com.saqib.school.fee.model;

import com.saqib.school.fee.entity.FineStructure;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FineStructureRequest {

    @NotNull(message = "Class ID is required")
    private Long classId;

    @NotNull(message = "Days after due is required")
    @Min(value = 1, message = "Days after due must be at least 1")
    private Integer daysAfterDue;

    @NotNull(message = "Fine type is required")
    private FineStructure.FineType fineType;

    @NotNull(message = "Fine value is required")
    @DecimalMin(value = "0.01", message = "Fine value must be greater than 0")
    private BigDecimal fineValue;
}
