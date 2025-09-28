package com.saqib.school.academic.controller;

import com.saqib.school.academic.entity.AcademicSession;
import com.saqib.school.academic.model.AcademicSessionRequest;
import com.saqib.school.academic.model.AcademicSessionResponse;
import com.saqib.school.academic.service.AcademicSessionService;
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

@RestController
@RequestMapping("/api/academic/sessions")
@RequiredArgsConstructor
@Tag(name = "Academic Session Management", description = "Academic session CRUD and status management")
public class AcademicSessionController {

    private final AcademicSessionService sessionService;

    @PostMapping
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Create academic session")
    public ResponseEntity<ApiResponse<AcademicSessionResponse>> createSession(@Valid @RequestBody AcademicSessionRequest request) {
        AcademicSessionResponse response = sessionService.createSession(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Academic session created successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'ACCOUNTANT', 'CLASS_TEACHER', 'SUBJECT_TEACHER')")
    @Operation(summary = "Get session by ID")
    public ResponseEntity<ApiResponse<AcademicSessionResponse>> getSessionById(@PathVariable Long id) {
        AcademicSessionResponse response = sessionService.getSessionById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/name/{sessionName}")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'ACCOUNTANT', 'CLASS_TEACHER', 'SUBJECT_TEACHER')")
    @Operation(summary = "Get session by name")
    public ResponseEntity<ApiResponse<AcademicSessionResponse>> getSessionByName(@PathVariable String sessionName) {
        AcademicSessionResponse response = sessionService.getSessionByName(sessionName);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'ACCOUNTANT', 'CLASS_TEACHER', 'SUBJECT_TEACHER')")
    @Operation(summary = "Get all sessions")
    public ResponseEntity<ApiResponse<PageResponse<AcademicSessionResponse>>> getAllSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "sessionName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PageResponse<AcademicSessionResponse> response = sessionService.getAllSessions(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'ACCOUNTANT')")
    @Operation(summary = "Get sessions by status")
    public ResponseEntity<ApiResponse<PageResponse<AcademicSessionResponse>>> getSessionsByStatus(
            @PathVariable AcademicSession.SessionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PageResponse<AcademicSessionResponse> response = sessionService.getSessionsByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'ACCOUNTANT', 'CLASS_TEACHER', 'SUBJECT_TEACHER')")
    @Operation(summary = "Get active session")
    public ResponseEntity<ApiResponse<AcademicSessionResponse>> getActiveSession() {
        AcademicSessionResponse response = sessionService.getActiveSession();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
    @Operation(summary = "Get upcoming session")
    public ResponseEntity<ApiResponse<AcademicSessionResponse>> getUpcomingSession() {
        AcademicSessionResponse response = sessionService.getUpcomingSession();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Update session")
    public ResponseEntity<ApiResponse<AcademicSessionResponse>> updateSession(
            @PathVariable Long id,
            @Valid @RequestBody AcademicSessionRequest request) {

        AcademicSessionResponse response = sessionService.updateSession(id, request);
        return ResponseEntity.ok(ApiResponse.success("Academic session updated successfully", response));
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('PRINCIPAL')")
    @Operation(summary = "Activate session")
    public ResponseEntity<ApiResponse<String>> activateSession(@PathVariable Long id) {
        sessionService.activateSession(id);
        return ResponseEntity.ok(ApiResponse.success("Academic session activated successfully"));
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('PRINCIPAL')")
    @Operation(summary = "Deactivate session")
    public ResponseEntity<ApiResponse<String>> deactivateSession(@PathVariable Long id) {
        sessionService.deactivateSession(id);
        return ResponseEntity.ok(ApiResponse.success("Academic session deactivated successfully"));
    }

    @PutMapping("/{id}/archive")
    @PreAuthorize("hasRole('PRINCIPAL')")
    @Operation(summary = "Archive session")
    public ResponseEntity<ApiResponse<String>> archiveSession(@PathVariable Long id) {
        sessionService.archiveSession(id);
        return ResponseEntity.ok(ApiResponse.success("Academic session archived successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL')")
    @Operation(summary = "Delete session")
    public ResponseEntity<ApiResponse<String>> deleteSession(@PathVariable Long id) {
        sessionService.deleteSession(id);
        return ResponseEntity.ok(ApiResponse.success("Academic session deleted successfully"));
    }
}
