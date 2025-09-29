package com.saqib.school.fee.controller;

import com.saqib.school.common.dto.ApiResponse;
import com.saqib.school.fee.model.DefaulterReportRequest;
import com.saqib.school.fee.model.DefaulterReportResponse;
import com.saqib.school.fee.service.DefaulterReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/defaulter-reports")
@RequiredArgsConstructor
@Tag(name = "Defaulter Reports", description = "Defaulter reporting and analysis")
public class DefaulterReportController {

    private final DefaulterReportService defaulterReportService;

    @PostMapping("/generate")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Generate defaulter report", description = "Generate comprehensive defaulter report")
    public ResponseEntity<ApiResponse<DefaulterReportResponse>> generateDefaulterReport(@RequestBody DefaulterReportRequest request) {
        DefaulterReportResponse response = defaulterReportService.generateDefaulterReport(request);
        return ResponseEntity.ok(ApiResponse.success("Defaulter report generated successfully", response));
    }
}
