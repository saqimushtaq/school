package com.saqib.school.fee.model;

import com.saqib.school.fee.entity.FeeVoucher;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeVoucherResponse {
    private Long id;
    private String voucherNumber;
    private Long studentId;
    private String studentName;
    private String studentRegistrationNumber;
    private FeeVoucher.VoucherType voucherType;
    private String monthYear;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal fineAmount;
    private BigDecimal remainingAmount;
    private FeeVoucher.VoucherStatus status;
    private LocalDate paymentDate;
    private String notes;
    private String createdByName;
    private List<VoucherDetailResponse> voucherDetails;
    private List<PaymentResponse> payments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VoucherDetailResponse {
        private Long id;
        private String feeCategoryName;
        private BigDecimal originalAmount;
        private BigDecimal discountAmount;
        private BigDecimal finalAmount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentResponse {
        private Long id;
        private BigDecimal amount;
        private LocalDate paymentDate;
        private String paymentMethod;
        private String referenceNumber;
        private String receivedByName;
    }
}
