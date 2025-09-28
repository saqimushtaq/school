package com.saqib.school.fee.controller;

import com.saqib.school.common.dto.ApiResponse;
import com.saqib.school.fee.model.FineCalculationRequest;
import com.saqib.school.fee.service.FineCalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/fine-calculations")
@RequiredArgsConstructor
@Tag(name = "Fine Calculation", description = "Fine calculation and management operations")
public class FineCalculationController {

    private final FineCalculationService fineCalculationService;

    @PostMapping("/calculate")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Calculate fines", description = "Calculate fines for specified vouchers")
    public ResponseEntity<ApiResponse<Map<Long, BigDecimal>>> calculateFines(@Valid @RequestBody FineCalculationRequest request) {
        Map<Long, BigDecimal> calculations = fineCalculationService.calculateFines(request);
        return ResponseEntity.ok(ApiResponse.success("Fines calculated successfully", calculations));
    }

    @PostMapping("/apply")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Apply calculated fines", description = "Apply calculated fines to vouchers")
    public ResponseEntity<ApiResponse<String>> applyCalculatedFines(@Valid @RequestBody FineCalculationRequest request) {
        fineCalculationService.applyCalculatedFines(request);
        return ResponseEntity.ok(ApiResponse.success("Fines applied successfully"));
    }

    @PatchMapping("/waive/{voucherId}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Waive fine", description = "Waive fine for specific voucher")
    public ResponseEntity<ApiResponse<String>> waiveFine(
        @PathVariable Long voucherId,
        @RequestParam String reason) {

        fineCalculationService.waiveFine(voucherId, reason);
        return ResponseEntity.ok(ApiResponse.success("Fine waived successfully"));
    }
}
