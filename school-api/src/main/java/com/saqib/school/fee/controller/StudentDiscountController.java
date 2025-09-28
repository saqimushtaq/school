package com.saqib.school.fee.controller;

import com.saqib.school.common.dto.ApiResponse;
import com.saqib.school.common.dto.PageResponse;
import com.saqib.school.fee.model.StudentDiscountRequest;
import com.saqib.school.fee.model.StudentDiscountResponse;
import com.saqib.school.fee.model.StudentDiscountUpdateRequest;
import com.saqib.school.fee.service.StudentDiscountService;
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
@RequestMapping("/api/student-discounts")
@RequiredArgsConstructor
@Tag(name = "Student Discount Management", description = "Student discount operations")
public class StudentDiscountController {

    private final StudentDiscountService studentDiscountService;

    @PostMapping
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Create student discount", description = "Create a new student discount")
    public ResponseEntity<ApiResponse<StudentDiscountResponse>> createStudentDiscount(@Valid @RequestBody StudentDiscountRequest request) {
        StudentDiscountResponse response = studentDiscountService.createStudentDiscount(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Student discount created successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get student discount by ID", description = "Retrieve student discount details by ID")
    public ResponseEntity<ApiResponse<StudentDiscountResponse>> getStudentDiscountById(@PathVariable Long id) {
        StudentDiscountResponse response = studentDiscountService.getStudentDiscountById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get student discounts", description = "Retrieve all discounts for a specific student")
    public ResponseEntity<ApiResponse<List<StudentDiscountResponse>>> getStudentDiscounts(@PathVariable Long studentId) {
        List<StudentDiscountResponse> response = studentDiscountService.getStudentDiscounts(studentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get discounts by category", description = "Retrieve discounts for a specific fee category")
    public ResponseEntity<ApiResponse<PageResponse<StudentDiscountResponse>>> getDiscountsByCategory(
        @PathVariable Long categoryId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PageResponse<StudentDiscountResponse> response = studentDiscountService.getDiscountsByCategory(categoryId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/calculate")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Calculate discount amount", description = "Calculate discount amount for student and category")
    public ResponseEntity<ApiResponse<BigDecimal>> calculateDiscountAmount(
        @RequestParam Long studentId,
        @RequestParam Long categoryId,
        @RequestParam BigDecimal originalAmount,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate calculationDate = date != null ? date : LocalDate.now();
        BigDecimal discount = studentDiscountService.calculateDiscountAmount(studentId, categoryId, originalAmount, calculationDate);
        return ResponseEntity.ok(ApiResponse.success(discount));
    }

    @GetMapping("/valid")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get valid discount", description = "Get valid discount for student and category on specific date")
    public ResponseEntity<ApiResponse<StudentDiscountResponse>> getValidDiscount(
        @RequestParam Long studentId,
        @RequestParam Long categoryId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate checkDate = date != null ? date : LocalDate.now();
        StudentDiscountResponse response = studentDiscountService.getValidDiscount(studentId, categoryId, checkDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Update student discount", description = "Update student discount details")
    public ResponseEntity<ApiResponse<StudentDiscountResponse>> updateStudentDiscount(
        @PathVariable Long id,
        @Valid @RequestBody StudentDiscountUpdateRequest request) {

        StudentDiscountResponse response = studentDiscountService.updateStudentDiscount(id, request);
        return ResponseEntity.ok(ApiResponse.success("Student discount updated successfully", response));
    }

    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Toggle discount status", description = "Toggle active/inactive status of discount")
    public ResponseEntity<ApiResponse<String>> toggleStudentDiscountStatus(@PathVariable Long id) {
        studentDiscountService.toggleStudentDiscountStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Student discount status toggled successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL')")
    @Operation(summary = "Delete student discount", description = "Delete student discount")
    public ResponseEntity<ApiResponse<String>> deleteStudentDiscount(@PathVariable Long id) {
        studentDiscountService.deleteStudentDiscount(id);
        return ResponseEntity.ok(ApiResponse.success("Student discount deleted successfully"));
    }

    @PostMapping("/expire-old")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Expire old discounts", description = "Mark expired discounts as inactive")
    public ResponseEntity<ApiResponse<String>> expireOldDiscounts() {
        studentDiscountService.expireOldDiscounts();
        return ResponseEntity.ok(ApiResponse.success("Old discounts expired successfully"));
    }
}
