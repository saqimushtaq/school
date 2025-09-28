package com.saqib.school.fee.model;

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
public class DefaulterReportResponse {
    private LocalDate reportDate;
    private Integer totalDefaulters;
    private BigDecimal totalOutstandingAmount;
    private List<DefaulterInfo> defaulters;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DefaulterInfo {
        private Long studentId;
        private String studentName;
        private String registrationNumber;
        private String className;
        private String guardianName;
        private String guardianPhone;
        private Integer totalOverdueVouchers;
        private BigDecimal totalOutstandingAmount;
        private BigDecimal totalFineAmount;
        private LocalDate oldestDueDate;
        private Integer daysSinceOldestDue;
        private List<OverdueVoucherInfo> overdueVouchers;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OverdueVoucherInfo {
        private Long voucherId;
        private String voucherNumber;
        private String voucherType;
        private String monthYear;
        private LocalDate dueDate;
        private Integer daysOverdue;
        private BigDecimal totalAmount;
        private BigDecimal fineAmount;
        private BigDecimal remainingAmount;
    }
}
