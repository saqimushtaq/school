package com.saqib.school.fee.controller;

import com.saqib.school.common.dto.ApiResponse;
import com.saqib.school.fee.service.FeeReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

@RestController
@RequestMapping("/api/fee-reports")
@RequiredArgsConstructor
@Tag(name = "Fee Reports", description = "Financial reporting and analytics")
public class FeeReportController {

    private final FeeReportService feeReportService;

    @GetMapping("/monthly/{year}/{month}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Generate monthly collection report", description = "Generate monthly fee collection report")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generateMonthlyCollectionReport(
        @PathVariable int year,
        @PathVariable int month) {

        YearMonth yearMonth = YearMonth.of(year, month);
        Map<String, Object> report = feeReportService.generateMonthlyCollectionReport(yearMonth);
        return ResponseEntity.ok(ApiResponse.success("Monthly collection report generated successfully", report));
    }

    @GetMapping("/annual/{year}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Generate annual collection summary", description = "Generate annual fee collection summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generateAnnualCollectionSummary(@PathVariable int year) {
        Map<String, Object> summary = feeReportService.generateAnnualCollectionSummary(year);
        return ResponseEntity.ok(ApiResponse.success("Annual collection summary generated successfully", summary));
    }

    @GetMapping("/collection-summary")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Generate collection summary", description = "Generate collection summary for date range")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generateCollectionSummary(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Map<String, Object> summary = feeReportService.generateCollectionSummary(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Collection summary generated successfully", summary));
    }
}
