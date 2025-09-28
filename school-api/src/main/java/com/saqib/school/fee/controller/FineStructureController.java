package com.saqib.school.fee.controller;

import com.saqib.school.common.dto.ApiResponse;
import com.saqib.school.common.dto.PageResponse;
import com.saqib.school.fee.model.FineStructureRequest;
import com.saqib.school.fee.model.FineStructureResponse;
import com.saqib.school.fee.service.FineStructureService;
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
@RequestMapping("/api/fine-structures")
@RequiredArgsConstructor
@Tag(name = "Fine Structure Management", description = "Fine structure configuration and management")
public class FineStructureController {

    private final FineStructureService fineStructureService;

    @PostMapping
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Create fine structure", description = "Create a new fine structure for class")
    public ResponseEntity<ApiResponse<FineStructureResponse>> createFineStructure(@Valid @RequestBody FineStructureRequest request) {
        FineStructureResponse response = fineStructureService.createFineStructure(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Fine structure created successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get fine structure by ID", description = "Retrieve fine structure details by ID")
    public ResponseEntity<ApiResponse<FineStructureResponse>> getFineStructureById(@PathVariable Long id) {
        FineStructureResponse response = fineStructureService.getFineStructureById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get fine structures by class", description = "Retrieve all fine structures for a specific class")
    public ResponseEntity<ApiResponse<List<FineStructureResponse>>> getFineStructuresByClass(@PathVariable Long classId) {
        List<FineStructureResponse> response = fineStructureService.getFineStructuresByClass(classId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/session/{sessionId}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get fine structures by session", description = "Retrieve fine structures for academic session")
    public ResponseEntity<ApiResponse<PageResponse<FineStructureResponse>>> getFineStructuresBySession(
        @PathVariable Long sessionId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("schoolClass.className", "daysAfterDue"));
        PageResponse<FineStructureResponse> response = fineStructureService.getFineStructuresBySession(sessionId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get active fine structures", description = "Retrieve active fine structures with pagination")
    public ResponseEntity<ApiResponse<PageResponse<FineStructureResponse>>> getActiveFineStructures(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("schoolClass.className", "daysAfterDue"));
        PageResponse<FineStructureResponse> response = fineStructureService.getActiveFineStructures(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/active/all")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER') or hasRole('ACCOUNTANT')")
    @Operation(summary = "Get all active fine structures", description = "Retrieve all active fine structures")
    public ResponseEntity<ApiResponse<List<FineStructureResponse>>> getAllActiveFineStructures() {
        List<FineStructureResponse> response = fineStructureService.getAllActiveFineStructures();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Update fine structure", description = "Update fine structure details")
    public ResponseEntity<ApiResponse<FineStructureResponse>> updateFineStructure(
        @PathVariable Long id,
        @Valid @RequestBody FineStructureRequest request) {

        FineStructureResponse response = fineStructureService.updateFineStructure(id, request);
        return ResponseEntity.ok(ApiResponse.success("Fine structure updated successfully", response));
    }

    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Toggle fine structure status", description = "Toggle active/inactive status")
    public ResponseEntity<ApiResponse<String>> toggleFineStructureStatus(@PathVariable Long id) {
        fineStructureService.toggleFineStructureStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Fine structure status toggled successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL')")
    @Operation(summary = "Delete fine structure", description = "Delete fine structure")
    public ResponseEntity<ApiResponse<String>> deleteFineStructure(@PathVariable Long id) {
        fineStructureService.deleteFineStructure(id);
        return ResponseEntity.ok(ApiResponse.success("Fine structure deleted successfully"));
    }
}
