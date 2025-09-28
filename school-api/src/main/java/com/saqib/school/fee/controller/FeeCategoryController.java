package com.saqib.school.fee.controller;

import com.saqib.school.common.dto.ApiResponse;
import com.saqib.school.common.dto.PageResponse;
import com.saqib.school.fee.model.FeeCategoryRequest;
import com.saqib.school.fee.model.FeeCategoryResponse;
import com.saqib.school.fee.model.FeeCategoryUpdateRequest;
import com.saqib.school.fee.service.FeeCategoryService;
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
@RequestMapping("/api/fee-categories")
@RequiredArgsConstructor
@Tag(name = "Fee Category Management", description = "Fee category CRUD operations")
public class FeeCategoryController {

    private final FeeCategoryService feeCategoryService;

    @PostMapping
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Create fee category", description = "Create a new fee category")
    public ResponseEntity<ApiResponse<FeeCategoryResponse>> createFeeCategory(@Valid @RequestBody FeeCategoryRequest request) {
        FeeCategoryResponse response = feeCategoryService.createFeeCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Fee category created successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get fee category by ID", description = "Retrieve fee category details by ID")
    public ResponseEntity<ApiResponse<FeeCategoryResponse>> getFeeCategoryById(@PathVariable Long id) {
        FeeCategoryResponse response = feeCategoryService.getFeeCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/name/{categoryName}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get fee category by name", description = "Retrieve fee category details by name")
    public ResponseEntity<ApiResponse<FeeCategoryResponse>> getFeeCategoryByName(@PathVariable String categoryName) {
        FeeCategoryResponse response = feeCategoryService.getFeeCategoryByName(categoryName);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get all fee categories", description = "Retrieve paginated list of fee categories")
    public ResponseEntity<ApiResponse<PageResponse<FeeCategoryResponse>>> getAllFeeCategories(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "categoryName") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PageResponse<FeeCategoryResponse> response = feeCategoryService.getAllFeeCategories(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get active fee categories", description = "Retrieve paginated list of active fee categories")
    public ResponseEntity<ApiResponse<PageResponse<FeeCategoryResponse>>> getActiveFeeCategories(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("categoryName"));
        PageResponse<FeeCategoryResponse> response = feeCategoryService.getActiveFeeCategories(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/active/all")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get all active fee categories", description = "Retrieve all active fee categories without pagination")
    public ResponseEntity<ApiResponse<List<FeeCategoryResponse>>> getAllActiveFeeCategories() {
        List<FeeCategoryResponse> response = feeCategoryService.getAllActiveFeeCategories();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Search fee categories", description = "Search fee categories by name or description")
    public ResponseEntity<ApiResponse<PageResponse<FeeCategoryResponse>>> searchFeeCategories(
        @RequestParam String searchTerm,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("categoryName"));
        PageResponse<FeeCategoryResponse> response = feeCategoryService.searchFeeCategories(searchTerm, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Update fee category", description = "Update fee category information")
    public ResponseEntity<ApiResponse<FeeCategoryResponse>> updateFeeCategory(
        @PathVariable Long id,
        @Valid @RequestBody FeeCategoryUpdateRequest request) {

        FeeCategoryResponse response = feeCategoryService.updateFeeCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success("Fee category updated successfully", response));
    }

    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Toggle fee category status", description = "Toggle active/inactive status of fee category")
    public ResponseEntity<ApiResponse<String>> toggleFeeCategoryStatus(@PathVariable Long id) {
        feeCategoryService.toggleFeeCategoryStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Fee category status toggled successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL')")
    @Operation(summary = "Delete fee category", description = "Delete fee category (only if not used)")
    public ResponseEntity<ApiResponse<String>> deleteFeeCategory(@PathVariable Long id) {
        feeCategoryService.deleteFeeCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Fee category deleted successfully"));
    }
}
