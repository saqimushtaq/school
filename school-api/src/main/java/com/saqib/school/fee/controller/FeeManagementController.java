package com.saqib.school.fee.controller;

import com.saqib.school.common.dto.ApiResponse;
import com.saqib.school.fee.model.*;
import com.saqib.school.fee.service.FeeManagementFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fee-management")
@RequiredArgsConstructor
@Tag(name = "Fee Management Facade", description = "High-level fee management operations")
public class FeeManagementController {

    private final FeeManagementFacadeService feeManagementFacadeService;

    @PostMapping("/admission-fee")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Process admission fee", description = "Process admission fee for student")
    public ResponseEntity<ApiResponse<FeeVoucherResponse>> processAdmissionFee(
        @RequestParam Long studentId,
        @RequestBody List<FeeVoucherRequest.VoucherDetailRequest> feeDetails,
        @RequestParam LocalDate dueDate) {

        FeeVoucherResponse response = feeManagementFacadeService.processAdmissionFee(studentId, feeDetails, dueDate);
        return ResponseEntity.ok(ApiResponse.success("Admission fee processed successfully", response));
    }

    @PostMapping("/monthly-collection")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Process monthly fee collection", description = "Generate monthly vouchers and process fines")
    public ResponseEntity<ApiResponse<Map<String, Object>>> processMonthlyFeeCollection(
        @Valid @RequestBody MonthlyVoucherGenerationRequest request) {

        Map<String, Object> result = feeManagementFacadeService.processMonthlyFeeCollection(request);
        return ResponseEntity.ok(ApiResponse.success("Monthly fee collection processed successfully", result));
    }

    @PostMapping("/payment-with-fine-check")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Process payment with fine check", description = "Process payment after checking and applying fines")
    public ResponseEntity<ApiResponse<Map<String, Object>>> processFeePaymentWithFineCheck(
        @Valid @RequestBody FeePaymentRequest paymentRequest) {

        Map<String, Object> result = feeManagementFacadeService.processFeePaymentWithFineCheck(paymentRequest);
        return ResponseEntity.ok(ApiResponse.success("Payment processed successfully", result));
    }

    @PostMapping("/comprehensive-defaulter-report")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Generate comprehensive defaulter report", description = "Generate detailed defaulter report with analytics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generateComprehensiveDefaulterReport(
        @Valid @RequestBody DefaulterReportRequest request) {

        Map<String, Object> report = feeManagementFacadeService.generateComprehensiveDefaulterReport(request);
        return ResponseEntity.ok(ApiResponse.success("Comprehensive defaulter report generated successfully", report));
    }

    @GetMapping("/monthly-financial-summary/{year}/{month}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Generate monthly financial summary", description = "Generate complete monthly financial summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generateMonthlyFinancialSummary(
        @PathVariable int year,
        @PathVariable int month) {

        YearMonth yearMonth = YearMonth.of(year, month);
        Map<String, Object> summary = feeManagementFacadeService.generateMonthlyFinancialSummary(yearMonth);
        return ResponseEntity.ok(ApiResponse.success("Monthly financial summary generated successfully", summary));
    }

    @PostMapping("/bulk-discount")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Apply bulk discount", description = "Apply discount to multiple students")
    public ResponseEntity<ApiResponse<Map<String, Object>>> applyBulkDiscount(
        @RequestParam List<Long> studentIds,
        @RequestParam Long categoryId,
        @Valid @RequestBody StudentDiscountRequest discountTemplate) {

        Map<String, Object> result = feeManagementFacadeService.applyBulkDiscount(studentIds, categoryId, discountTemplate);
        return ResponseEntity.ok(ApiResponse.success("Bulk discount applied successfully", result));
    }

    @PostMapping("/daily-maintenance")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Process daily fee maintenance", description = "Run daily fee maintenance tasks")
    public ResponseEntity<ApiResponse<Map<String, Object>>> processDailyFeeMaintenance() {
        Map<String, Object> result = feeManagementFacadeService.processDailyFeeMaintenance();
        return ResponseEntity.ok(ApiResponse.success("Daily fee maintenance completed successfully", result));
    }
}
