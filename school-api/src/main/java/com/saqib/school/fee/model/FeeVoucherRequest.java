package com.saqib.school.fee.model;

import com.saqib.school.fee.entity.FeeVoucher;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeVoucherRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Voucher type is required")
    private FeeVoucher.VoucherType voucherType;

    @Size(max = 7, message = "Month year must be in MM-YYYY format")
    private String monthYear; // For monthly vouchers

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;

    @NotEmpty(message = "At least one voucher detail is required")
    @Valid
    private List<VoucherDetailRequest> voucherDetails;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VoucherDetailRequest {
        @NotNull(message = "Fee category ID is required")
        private Long feeCategoryId;

        @NotNull(message = "Original amount is required")
        @DecimalMin(value = "0.01", message = "Original amount must be greater than 0")
        private BigDecimal originalAmount;

        @Builder.Default
        private BigDecimal discountAmount = BigDecimal.ZERO;
    }
}
