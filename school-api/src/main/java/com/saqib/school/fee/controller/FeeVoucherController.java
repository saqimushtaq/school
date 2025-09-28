package com.saqib.school.fee.controller;

import com.saqib.school.common.dto.ApiResponse;
import com.saqib.school.common.dto.PageResponse;
import com.saqib.school.fee.entity.FeeVoucher;
import com.saqib.school.fee.model.FeeVoucherRequest;
import com.saqib.school.fee.model.FeeVoucherResponse;
import com.saqib.school.fee.model.MonthlyVoucherGenerationRequest;
import com.saqib.school.fee.service.FeeVoucherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/fee-vouchers")
@RequiredArgsConstructor
@Tag(name = "Fee Voucher Management", description = "Fee voucher operations and generation")
public class FeeVoucherController {

    private final FeeVoucherService feeVoucherService;

    @PostMapping
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Create fee voucher", description = "Create a new fee voucher")
    public ResponseEntity<ApiResponse<FeeVoucherResponse>> createFeeVoucher(@Valid @RequestBody FeeVoucherRequest request) {
        FeeVoucherResponse response = feeVoucherService.createFeeVoucher(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Fee voucher created successfully", response));
    }

    @PostMapping("/generate-monthly")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Generate monthly vouchers", description = "Generate monthly fee vouchers for students")
    public ResponseEntity<ApiResponse<List<FeeVoucherResponse>>> generateMonthlyVouchers(
        @Valid @RequestBody MonthlyVoucherGenerationRequest request) {

        List<FeeVoucherResponse> response = feeVoucherService.generateMonthlyVouchers(request);
        return ResponseEntity.ok(ApiResponse.success("Monthly vouchers generated successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get fee voucher by ID", description = "Retrieve fee voucher details by ID")
    public ResponseEntity<ApiResponse<FeeVoucherResponse>> getFeeVoucherById(@PathVariable Long id) {
        FeeVoucherResponse response = feeVoucherService.getFeeVoucherById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/number/{voucherNumber}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get fee voucher by number", description = "Retrieve fee voucher by voucher number")
    public ResponseEntity<ApiResponse<FeeVoucherResponse>> getFeeVoucherByNumber(@PathVariable String voucherNumber) {
        FeeVoucherResponse response = feeVoucherService.getFeeVoucherByNumber(voucherNumber);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get student vouchers", description = "Retrieve all vouchers for a specific student")
    public ResponseEntity<ApiResponse<PageResponse<FeeVoucherResponse>>> getStudentVouchers(
        @PathVariable Long studentId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("issueDate").descending());
        PageResponse<FeeVoucherResponse> response = feeVoucherService.getStudentVouchers(studentId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get vouchers by status", description = "Retrieve vouchers filtered by status")
    public ResponseEntity<ApiResponse<PageResponse<FeeVoucherResponse>>> getVouchersByStatus(
        @PathVariable FeeVoucher.VoucherStatus status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("issueDate").descending());
        PageResponse<FeeVoucherResponse> response = feeVoucherService.getVouchersByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get vouchers by type", description = "Retrieve vouchers filtered by type")
    public ResponseEntity<ApiResponse<PageResponse<FeeVoucherResponse>>> getVouchersByType(
        @PathVariable FeeVoucher.VoucherType type,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("issueDate").descending());
        PageResponse<FeeVoucherResponse> response = feeVoucherService.getVouchersByType(type, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/month/{monthYear}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get vouchers by month", description = "Retrieve vouchers for specific month-year")
    public ResponseEntity<ApiResponse<PageResponse<FeeVoucherResponse>>> getVouchersByMonthYear(
        @PathVariable String monthYear,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("issueDate").descending());
        PageResponse<FeeVoucherResponse> response = feeVoucherService.getVouchersByMonthYear(monthYear, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get vouchers by date range", description = "Retrieve vouchers within date range")
    public ResponseEntity<ApiResponse<PageResponse<FeeVoucherResponse>>> getVouchersByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("issueDate").descending());
        PageResponse<FeeVoucherResponse> response = feeVoucherService.getVouchersByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Cancel fee voucher", description = "Cancel a fee voucher with reason")
    public ResponseEntity<ApiResponse<String>> cancelFeeVoucher(
        @PathVariable Long id,
        @RequestParam String reason) {

        feeVoucherService.cancelFeeVoucher(id, reason);
        return ResponseEntity.ok(ApiResponse.success("Fee voucher cancelled successfully"));
    }

    @PostMapping("/process-overdue")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Process overdue vouchers", description = "Mark overdue vouchers as overdue status")
    public ResponseEntity<ApiResponse<String>> processOverdueVouchers() {
        feeVoucherService.processOverdueVouchers();
        return ResponseEntity.ok(ApiResponse.success("Overdue vouchers processed successfully"));
    }

    @GetMapping("/statistics/pending-count")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get pending vouchers count", description = "Get count of pending vouchers")
    public ResponseEntity<ApiResponse<Long>> getPendingVouchersCount() {
        long count = feeVoucherService.getPendingVouchersCount();
      return ResponseEntity.ok(ApiResponse.success(count));
    }

  @GetMapping("/statistics/collection")
  @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
  @Operation(summary = "Get collection for period", description = "Get total collection amount for date range")
  public ResponseEntity<ApiResponse<BigDecimal>> getTotalCollectionForPeriod(
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

    BigDecimal total = feeVoucherService.getTotalCollectionForPeriod(startDate, endDate);
    return ResponseEntity.ok(ApiResponse.success(total));
  }
}
