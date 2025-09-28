package com.saqib.school.academic.controller;

import com.saqib.school.academic.model.SubjectRequest;
import com.saqib.school.academic.model.SubjectResponse;
import com.saqib.school.academic.service.SubjectService;
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
@RequestMapping("/api/academic/subjects")
@RequiredArgsConstructor
@Tag(name = "Subject Management", description = "Subject CRUD and management")
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Create subject")
    public ResponseEntity<ApiResponse<SubjectResponse>> createSubject(@Valid @RequestBody SubjectRequest request) {
        SubjectResponse response = subjectService.createSubject(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subject created successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'ACCOUNTANT', 'CLASS_TEACHER', 'SUBJECT_TEACHER')")
    @Operation(summary = "Get subject by ID")
    public ResponseEntity<ApiResponse<SubjectResponse>> getSubjectById(@PathVariable Long id) {
        SubjectResponse response = subjectService.getSubjectById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/name/{subjectName}")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'ACCOUNTANT', 'CLASS_TEACHER', 'SUBJECT_TEACHER')")
    @Operation(summary = "Get subject by name")
    public ResponseEntity<ApiResponse<SubjectResponse>> getSubjectByName(@PathVariable String subjectName) {
        SubjectResponse response = subjectService.getSubjectByName(subjectName);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/code/{subjectCode}")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'ACCOUNTANT', 'CLASS_TEACHER', 'SUBJECT_TEACHER')")
    @Operation(summary = "Get subject by code")
    public ResponseEntity<ApiResponse<SubjectResponse>> getSubjectByCode(@PathVariable String subjectCode) {
        SubjectResponse response = subjectService.getSubjectByCode(subjectCode);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'ACCOUNTANT', 'CLASS_TEACHER', 'SUBJECT_TEACHER')")
    @Operation(summary = "Get all subjects")
    public ResponseEntity<ApiResponse<PageResponse<SubjectResponse>>> getAllSubjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "subjectName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PageResponse<SubjectResponse> response = subjectService.getAllSubjects(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'ACCOUNTANT', 'CLASS_TEACHER', 'SUBJECT_TEACHER')")
    @Operation(summary = "Get active subjects")
    public ResponseEntity<ApiResponse<List<SubjectResponse>>> getActiveSubjects() {
        List<SubjectResponse> response = subjectService.getActiveSubjects();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'ACCOUNTANT')")
    @Operation(summary = "Get subjects by status")
    public ResponseEntity<ApiResponse<PageResponse<SubjectResponse>>> getSubjectsByStatus(
            @RequestParam Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("subjectName"));
        PageResponse<SubjectResponse> response = subjectService.getSubjectsByStatus(isActive, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Update subject")
    public ResponseEntity<ApiResponse<SubjectResponse>> updateSubject(
            @PathVariable Long id,
            @Valid @RequestBody SubjectRequest request) {

        SubjectResponse response = subjectService.updateSubject(id, request);
        return ResponseEntity.ok(ApiResponse.success("Subject updated successfully", response));
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Activate subject")
    public ResponseEntity<ApiResponse<String>> activateSubject(@PathVariable Long id) {
        subjectService.activateSubject(id);
        return ResponseEntity.ok(ApiResponse.success("Subject activated successfully"));
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Deactivate subject")
    public ResponseEntity<ApiResponse<String>> deactivateSubject(@PathVariable Long id) {
        subjectService.deactivateSubject(id);
        return ResponseEntity.ok(ApiResponse.success("Subject deactivated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL')")
    @Operation(summary = "Delete subject")
    public ResponseEntity<ApiResponse<String>> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.ok(ApiResponse.success("Subject deleted successfully"));
    }
}
