package com.saqib.school.fee.service;

import com.saqib.school.common.audit.Auditable;
import com.saqib.school.fee.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeeManagementFacadeService {

    private final FeeVoucherService feeVoucherService;
    private final FeePaymentService feePaymentService;
    private final FineCalculationService fineCalculationService;
    private final DefaulterReportService defaulterReportService;
    private final FeeReportService feeReportService;
    private final StudentDiscountService studentDiscountService;

    @Transactional
    @Auditable(action = "PROCESS_ADMISSION_FEE", entityType = "FeeVoucher")
    public FeeVoucherResponse processAdmissionFee(Long studentId, List<FeeVoucherRequest.VoucherDetailRequest> feeDetails, LocalDate dueDate) {
        FeeVoucherRequest request = FeeVoucherRequest.builder()
            .studentId(studentId)
            .voucherType(com.saqib.school.fee.entity.FeeVoucher.VoucherType.ADMISSION)
            .dueDate(dueDate)
            .voucherDetails(feeDetails)
            .notes("Admission fee voucher")
            .build();

        return feeVoucherService.createFeeVoucher(request);
    }

    @Transactional
    @Auditable(action = "PROCESS_MONTHLY_FEE_COLLECTION", entityType = "FeeVoucher")
    public Map<String, Object> processMonthlyFeeCollection(MonthlyVoucherGenerationRequest request) {
        // Generate monthly vouchers
        List<FeeVoucherResponse> vouchers = feeVoucherService.generateMonthlyVouchers(request);

        // Calculate fines for overdue vouchers
        fineCalculationService.applyCalculatedFines(FineCalculationRequest.builder()
            .calculationDate(LocalDate.now())
            .voucherIds(vouchers.stream().map(FeeVoucherResponse::getId).toList())
            .build());

        Map<String, Object> result = Map.of(
            "generatedVouchers", vouchers.size(),
            "monthYear", request.getMonthYear(),
            "dueDate", request.getDueDate(),
            "vouchers", vouchers
        );

        log.info("Monthly fee collection processed for {} - Generated {} vouchers",
                 request.getMonthYear(), vouchers.size());

        return result;
    }

    @Transactional
    @Auditable(action = "PROCESS_FEE_PAYMENT_WITH_FINE_CHECK", entityType = "FeePayment")
    public Map<String, Object> processFeePaymentWithFineCheck(FeePaymentRequest paymentRequest) {
        // First check if fine needs to be applied
        FeeVoucherResponse voucher = feeVoucherService.getFeeVoucherById(paymentRequest.getVoucherId());

        if (voucher.getStatus().equals(com.saqib.school.fee.entity.FeeVoucher.VoucherStatus.PENDING) &&
            voucher.getDueDate().isBefore(LocalDate.now())) {

            // Apply fine if overdue
            fineCalculationService.applyCalculatedFines(FineCalculationRequest.builder()
                .calculationDate(LocalDate.now())
                .voucherIds(List.of(paymentRequest.getVoucherId()))
                .build());

            // Refresh voucher data
            voucher = feeVoucherService.getFeeVoucherById(paymentRequest.getVoucherId());
        }

        // Process payment
        FeePaymentResponse payment = feePaymentService.processFeePayment(paymentRequest);

        return Map.of(
            "payment", payment,
            "voucher", feeVoucherService.getFeeVoucherById(paymentRequest.getVoucherId()),
            "fineApplied", voucher.getFineAmount().compareTo(BigDecimal.ZERO) > 0
        );
    }

    @Transactional(readOnly = true)
    public Map<String, Object> generateComprehensiveDefaulterReport(DefaulterReportRequest request) {
        DefaulterReportResponse report = defaulterReportService.generateDefaulterReport(request);

        Map<String, Object> comprehensiveReport = Map.of(
            "defaulterReport", report,
            "summary", Map.of(
                "totalDefaulters", report.getTotalDefaulters(),
                "totalOutstanding", report.getTotalOutstandingAmount(),
                "averageOutstandingPerDefaulter", report.getTotalDefaulters() > 0 ?
                    report.getTotalOutstandingAmount().divide(BigDecimal.valueOf(report.getTotalDefaulters()), 2, java.math.RoundingMode.HALF_UP) :
                    BigDecimal.ZERO,
                "reportGeneratedOn", LocalDate.now()
            )
        );

        log.info("Comprehensive defaulter report generated - {} defaulters with total outstanding: {}",
                 report.getTotalDefaulters(), report.getTotalOutstandingAmount());

        return comprehensiveReport;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> generateMonthlyFinancialSummary(YearMonth month) {
        Map<String, Object> collectionReport = feeReportService.generateMonthlyCollectionReport(month);

        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        // Get defaulter statistics
        DefaulterReportResponse defaulterReport = defaulterReportService.generateDefaulterReport(
            DefaulterReportRequest.builder()
                .asOfDate(endDate)
                .build()
        );

        Map<String, Object> summary = Map.of(
            "month", month.toString(),
            "collection", collectionReport,
            "defaulterSummary", Map.of(
                "totalDefaulters", defaulterReport.getTotalDefaulters(),
                "totalOutstanding", defaulterReport.getTotalOutstandingAmount()
            ),
            "generatedOn", LocalDate.now()
        );

        return summary;
    }

    @Transactional
    @Auditable(action = "APPLY_BULK_DISCOUNT", entityType = "StudentDiscount")
    public Map<String, Object> applyBulkDiscount(List<Long> studentIds, Long categoryId,
                                                 StudentDiscountRequest discountTemplate) {
        int successCount = 0;
        int failureCount = 0;

        for (Long studentId : studentIds) {
            try {
                StudentDiscountRequest request = StudentDiscountRequest.builder()
                    .studentId(studentId)
                    .feeCategoryId(categoryId)
                    .discountType(discountTemplate.getDiscountType())
                    .discountValue(discountTemplate.getDiscountValue())
                    .reason(discountTemplate.getReason())
                    .validFrom(discountTemplate.getValidFrom())
                    .validTo(discountTemplate.getValidTo())
                    .build();

                studentDiscountService.createStudentDiscount(request);
                successCount++;
            } catch (Exception e) {
                log.warn("Failed to apply discount to student {}: {}", studentId, e.getMessage());
                failureCount++;
            }
        }

        Map<String, Object> result = Map.of(
            "totalProcessed", studentIds.size(),
            "successful", successCount,
            "failed", failureCount,
            "discountType", discountTemplate.getDiscountType(),
            "discountValue", discountTemplate.getDiscountValue()
        );

        log.info("Bulk discount applied - Success: {}, Failed: {}", successCount, failureCount);
        return result;
    }

    @Transactional
    @Auditable(action = "PROCESS_DAILY_FEE_MAINTENANCE", entityType = "FeeVoucher")
    public Map<String, Object> processDailyFeeMaintenance() {
        // Process overdue vouchers
        feeVoucherService.processOverdueVouchers();

        // Expire old discounts
        studentDiscountService.expireOldDiscounts();

        // Get statistics
        long pendingVouchers = feeVoucherService.getPendingVouchersCount();

        Map<String, Object> result = Map.of(
            "processedDate", LocalDate.now(),
            "overdueVouchersProcessed", true,
            "expiredDiscountsProcessed", true,
            "currentPendingVouchers", pendingVouchers
        );

        log.info("Daily fee maintenance completed - Pending vouchers: {}", pendingVouchers);
        return result;
    }
}
