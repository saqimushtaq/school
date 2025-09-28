package com.saqib.school.fee.controller;

import com.saqib.school.common.dto.ApiResponse;
import com.saqib.school.common.dto.PageResponse;
import com.saqib.school.fee.model.*;
import com.saqib.school.fee.service.FeeStructureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fee-structures")
@RequiredArgsConstructor
@Tag(name = "Fee Structure Management", description = "Fee structure configuration and management")
public class FeeStructureController {

    private final FeeStructureService feeStructureService;

    @PostMapping
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Create fee structure", description = "Create a new fee structure for class and category")
    public ResponseEntity<ApiResponse<FeeStructureResponse>> createFeeStructure(@Valid @RequestBody FeeStructureRequest request) {
        FeeStructureResponse response = feeStructureService.createFeeStructure(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Fee structure created successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get fee structure by ID", description = "Retrieve fee structure details by ID")
    public ResponseEntity<ApiResponse<FeeStructureResponse>> getFeeStructureById(@PathVariable Long id) {
        FeeStructureResponse response = feeStructureService.getFeeStructureById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get fee structures by class", description = "Retrieve all fee structures for a specific class")
    public ResponseEntity<ApiResponse<List<FeeStructureResponse>>> getFeeStructuresByClass(@PathVariable Long classId) {
        List<FeeStructureResponse> response = feeStructureService.getFeeStructuresByClass(classId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get fee structures by category", description = "Retrieve all fee structures for a specific category")
    public ResponseEntity<ApiResponse<List<FeeStructureResponse>>> getFeeStructuresByCategory(@PathVariable Long categoryId) {
        List<FeeStructureResponse> response = feeStructureService.getFeeStructuresByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/session/{sessionId}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get fee structures by session", description = "Retrieve fee structures for a specific academic session")
    public ResponseEntity<ApiResponse<PageResponse<FeeStructureResponse>>> getFeeStructuresBySession(
        @PathVariable Long sessionId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("schoolClass.className", "feeCategory.categoryName"));
        PageResponse<FeeStructureResponse> response = feeStructureService.getFeeStructuresBySession(sessionId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/active/all")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get all active fee structures", description = "Retrieve all active fee structures")
    public ResponseEntity<ApiResponse<List<FeeStructureResponse>>> getAllActiveFeeStructures() {
        List<FeeStructureResponse> response = feeStructureService.getAllActiveFeeStructures();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Update fee structure", description = "Update fee structure details")
    public ResponseEntity<ApiResponse<FeeStructureResponse>> updateFeeStructure(
        @PathVariable Long id,
        @Valid @RequestBody FeeStructureUpdateRequest request) {

        FeeStructureResponse response = feeStructureService.updateFeeStructure(id, request);
        return ResponseEntity.ok(ApiResponse.success("Fee structure updated successfully", response));
    }

    @PutMapping("/bulk-update")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Bulk update fee structures", description = "Update multiple fee structures at once")
    public ResponseEntity<ApiResponse<List<FeeStructureResponse>>> bulkUpdateFeeStructures(
        @Valid @RequestBody BulkFeeUpdateRequest request) {

        List<FeeStructureResponse> response = feeStructureService.bulkUpdateFeeStructures(request);
        return ResponseEntity.ok(ApiResponse.success("Fee structures updated successfully", response));
    }

    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Toggle fee structure status", description = "Toggle active/inactive status")
    public ResponseEntity<ApiResponse<String>> toggleFeeStructureStatus(@PathVariable Long id) {
        feeStructureService.toggleFeeStructureStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Fee structure status toggled successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL')")
    @Operation(summary = "Delete fee structure", description = "Delete fee structure")
    public ResponseEntity<ApiResponse<String>> deleteFeeStructure(@PathVariable Long id) {
        feeStructureService.deleteFeeStructure(id);
        return ResponseEntity.ok(ApiResponse.success("Fee structure deleted successfully"));
    }
}
