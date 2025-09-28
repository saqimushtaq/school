package com.saqib.school.fee.controller;

import com.saqib.school.common.dto.ApiResponse;
import com.saqib.school.common.dto.PageResponse;
import com.saqib.school.fee.entity.FeePayment;
import com.saqib.school.fee.model.FeePaymentRequest;
import com.saqib.school.fee.model.FeePaymentResponse;
import com.saqib.school.fee.service.FeePaymentService;
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
@RequestMapping("/api/fee-payments")
@RequiredArgsConstructor
@Tag(name = "Fee Payment Management", description = "Fee payment processing and tracking")
public class FeePaymentController {

    private final FeePaymentService feePaymentService;

    @PostMapping
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Process fee payment", description = "Process a new fee payment")
    public ResponseEntity<ApiResponse<FeePaymentResponse>> processFeePayment(@Valid @RequestBody FeePaymentRequest request) {
        FeePaymentResponse response = feePaymentService.processFeePayment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Fee payment processed successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get fee payment by ID", description = "Retrieve fee payment details by ID")
    public ResponseEntity<ApiResponse<FeePaymentResponse>> getFeePaymentById(@PathVariable Long id) {
        FeePaymentResponse response = feePaymentService.getFeePaymentById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/voucher/{voucherId}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get voucher payments", description = "Retrieve all payments for a specific voucher")
    public ResponseEntity<ApiResponse<List<FeePaymentResponse>>> getVoucherPayments(@PathVariable Long voucherId) {
        List<FeePaymentResponse> response = feePaymentService.getVoucherPayments(voucherId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get payments by date range", description = "Retrieve payments within date range")
    public ResponseEntity<ApiResponse<PageResponse<FeePaymentResponse>>> getPaymentsByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("paymentDate").descending());
        PageResponse<FeePaymentResponse> response = feePaymentService.getPaymentsByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/method/{method}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get payments by method", description = "Retrieve payments filtered by payment method")
    public ResponseEntity<ApiResponse<PageResponse<FeePaymentResponse>>> getPaymentsByMethod(
        @PathVariable FeePayment.PaymentMethod method,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("paymentDate").descending());
        PageResponse<FeePaymentResponse> response = feePaymentService.getPaymentsByMethod(method, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Get payments by user", description = "Retrieve payments received by specific user")
    public ResponseEntity<ApiResponse<PageResponse<FeePaymentResponse>>> getPaymentsByUser(
        @PathVariable Long userId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("paymentDate").descending());
        PageResponse<FeePaymentResponse> response = feePaymentService.getPaymentsByUser(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/reference/{referenceNumber}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get payments by reference", description = "Retrieve payments by reference number")
    public ResponseEntity<ApiResponse<List<FeePaymentResponse>>> getPaymentsByReference(@PathVariable String referenceNumber) {
        List<FeePaymentResponse> response = feePaymentService.getPaymentsByReference(referenceNumber);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/statistics/collection")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get collection total", description = "Get total collection amount for date range")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalCollectionForPeriod(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        BigDecimal total = feePaymentService.getTotalCollectionForPeriod(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(total));
    }
}
