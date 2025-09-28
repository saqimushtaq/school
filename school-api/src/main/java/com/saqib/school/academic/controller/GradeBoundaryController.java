package com.saqib.school.academic.controller;

import com.saqib.school.academic.entity.GradeBoundary;
import com.saqib.school.academic.service.GradeBoundaryService;
import com.saqib.school.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/academic/grade-boundaries")
@RequiredArgsConstructor
@Tag(name = "Grade Boundary Management", description = "Grade boundary configuration and grade calculation")
public class GradeBoundaryController {

    private final GradeBoundaryService gradeBoundaryService;

    @PostMapping
    @PreAuthorize("hasRole('PRINCIPAL')")
    @Operation(summary = "Create grade boundary")
    public ResponseEntity<ApiResponse<GradeBoundary>> createGradeBoundary(
            @RequestParam @NotBlank String grade,
            @RequestParam @NotNull @DecimalMin("0.00") @DecimalMax("100.00") BigDecimal minPercentage,
            @RequestParam @NotNull @DecimalMin("0.00") @DecimalMax("100.00") BigDecimal maxPercentage,
            @RequestParam(defaultValue = "true") Boolean isPassing) {

        GradeBoundary response = gradeBoundaryService.createGradeBoundary(grade, minPercentage, maxPercentage, isPassing);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Grade boundary created successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'ACCOUNTANT', 'CLASS_TEACHER', 'SUBJECT_TEACHER')")
    @Operation(summary = "Get all grade boundaries")
    public ResponseEntity<ApiResponse<List<GradeBoundary>>> getAllGradeBoundaries() {
        List<GradeBoundary> response = gradeBoundaryService.getAllGradeBoundaries();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'ACCOUNTANT', 'CLASS_TEACHER', 'SUBJECT_TEACHER')")
    @Operation(summary = "Get grade boundary by ID")
    public ResponseEntity<ApiResponse<GradeBoundary>> getGradeBoundaryById(@PathVariable Long id) {
        GradeBoundary response = gradeBoundaryService.getGradeBoundaryById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/calculate-grade")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'ACCOUNTANT', 'CLASS_TEACHER', 'SUBJECT_TEACHER')")
    @Operation(summary = "Calculate grade for percentage")
    public ResponseEntity<ApiResponse<String>> calculateGrade(
            @RequestParam @DecimalMin("0.00") @DecimalMax("100.00") BigDecimal percentage) {

        String grade = gradeBoundaryService.calculateGrade(percentage);
        return ResponseEntity.ok(ApiResponse.success("Grade calculated successfully", grade));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL')")
    @Operation(summary = "Update grade boundary")
    public ResponseEntity<ApiResponse<GradeBoundary>> updateGradeBoundary(
            @PathVariable Long id,
            @RequestParam @NotBlank String grade,
            @RequestParam @NotNull @DecimalMin("0.00") @DecimalMax("100.00") BigDecimal minPercentage,
            @RequestParam @NotNull @DecimalMin("0.00") @DecimalMax("100.00") BigDecimal maxPercentage,
            @RequestParam(defaultValue = "true") Boolean isPassing) {

        GradeBoundary response = gradeBoundaryService.updateGradeBoundary(id, grade, minPercentage, maxPercentage, isPassing);
        return ResponseEntity.ok(ApiResponse.success("Grade boundary updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL')")
    @Operation(summary = "Delete grade boundary")
    public ResponseEntity<ApiResponse<String>> deleteGradeBoundary(@PathVariable Long id) {
        gradeBoundaryService.deleteGradeBoundary(id);
        return ResponseEntity.ok(ApiResponse.success("Grade boundary deleted successfully"));
    }

    @PostMapping("/setup-defaults")
    @PreAuthorize("hasRole('PRINCIPAL')")
    @Operation(summary = "Setup default grade boundaries")
    public ResponseEntity<ApiResponse<List<GradeBoundary>>> setupDefaultGradeBoundaries() {
      List<GradeBoundary> response = gradeBoundaryService.setupDefaultGradeBoundaries();
      return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success("Default grade boundaries setup successfully", response));
    }
}
