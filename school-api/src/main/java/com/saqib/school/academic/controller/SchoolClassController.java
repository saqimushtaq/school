package com.saqib.school.academic.controller;

import com.saqib.school.academic.model.SchoolClassRequest;
import com.saqib.school.academic.model.SchoolClassResponse;
import com.saqib.school.academic.service.SchoolClassService;
import com.saqib.school.common.dto.ApiResponse;
import com.saqib.school.common.dto.PageResponse;
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
@RequestMapping("/api/academic/classes")
@RequiredArgsConstructor
@Tag(name = "Class Management", description = "School class CRUD and management")
public class SchoolClassController {

    private final SchoolClassService classService;

    @PostMapping
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Create class")
    public ResponseEntity<ApiResponse<SchoolClassResponse>> createClass(@Valid @RequestBody SchoolClassRequest request) {
        SchoolClassResponse response = classService.createClass(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Class created successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'ACCOUNTANT', 'CLASS_TEACHER', 'SUBJECT_TEACHER')")
    @Operation(summary = "Get class by ID")
    public ResponseEntity<ApiResponse<SchoolClassResponse>> getClassById(@PathVariable Long id) {
        SchoolClassResponse response = classService.getClassById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'ACCOUNTANT', 'CLASS_TEACHER', 'SUBJECT_TEACHER')")
    @Operation(summary = "Get all classes")
    public ResponseEntity<ApiResponse<PageResponse<SchoolClassResponse>>> getAllClasses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "className") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PageResponse<SchoolClassResponse> response = classService.getAllClasses(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/session/{sessionId}")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'ACCOUNTANT', 'CLASS_TEACHER', 'SUBJECT_TEACHER')")
    @Operation(summary = "Get classes by session")
    public ResponseEntity<ApiResponse<PageResponse<SchoolClassResponse>>> getClassesBySession(
            @PathVariable Long sessionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("className", "section"));
        PageResponse<SchoolClassResponse> response = classService.getClassesBySession(sessionId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/session/{sessionId}/active")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'ACCOUNTANT', 'CLASS_TEACHER', 'SUBJECT_TEACHER')")
    @Operation(summary = "Get active classes by session")
    public ResponseEntity<ApiResponse<List<SchoolClassResponse>>> getActiveClassesBySession(@PathVariable Long sessionId) {
        List<SchoolClassResponse> response = classService.getActiveClassesBySession(sessionId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Update class")
    public ResponseEntity<ApiResponse<SchoolClassResponse>> updateClass(
            @PathVariable Long id,
            @Valid @RequestBody SchoolClassRequest request) {

        SchoolClassResponse response = classService.updateClass(id, request);
        return ResponseEntity.ok(ApiResponse.success("Class updated successfully", response));
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Activate class")
    public ResponseEntity<ApiResponse<String>> activateClass(@PathVariable Long id) {
        classService.activateClass(id);
        return ResponseEntity.ok(ApiResponse.success("Class activated successfully"));
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Deactivate class")
    public ResponseEntity<ApiResponse<String>> deactivateClass(@PathVariable Long id) {
        classService.deactivateClass(id);
        return ResponseEntity.ok(ApiResponse.success("Class deactivated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL')")
    @Operation(summary = "Delete class")
    public ResponseEntity<ApiResponse<String>> deleteClass(@PathVariable Long id) {
        classService.deleteClass(id);
        return ResponseEntity.ok(ApiResponse.success("Class deleted successfully"));
    }
}
