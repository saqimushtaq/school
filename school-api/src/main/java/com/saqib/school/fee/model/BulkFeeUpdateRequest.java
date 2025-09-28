package com.saqib.school.fee.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkFeeUpdateRequest {

    @NotEmpty(message = "At least one fee update is required")
    @Valid
    private List<FeeUpdateItem> feeUpdates;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeeUpdateItem {
        @NotNull(message = "Class ID is required")
        private Long classId;

        @NotNull(message = "Fee category ID is required")
        private Long feeCategoryId;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        private BigDecimal amount;
    }
}
