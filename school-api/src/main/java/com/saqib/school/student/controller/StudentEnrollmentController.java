package com.saqib.school.student.controller;

import com.saqib.school.common.dto.ApiResponse;
import com.saqib.school.common.dto.PageResponse;
import com.saqib.school.student.entity.StudentEnrollment;
import com.saqib.school.student.model.StudentEnrollmentRequest;
import com.saqib.school.student.model.StudentEnrollmentResponse;
import com.saqib.school.student.service.StudentEnrollmentService;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@Tag(name = "Student Enrollment Management", description = "Student enrollment operations")
public class StudentEnrollmentController {

    private final StudentEnrollmentService enrollmentService;

    @PostMapping
    @Operation(summary = "Enroll student", description = "Enroll student in class")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
    public ResponseEntity<ApiResponse<StudentEnrollmentResponse>> enrollStudent(
            @Valid @RequestBody StudentEnrollmentRequest request) {

        StudentEnrollmentResponse response = enrollmentService.enrollStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Student enrolled successfully", response));
    }

    @GetMapping("/{enrollmentId}")
    @Operation(summary = "Get enrollment by ID", description = "Get enrollment details by ID")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'CLASS_TEACHER')")
    public ResponseEntity<ApiResponse<StudentEnrollmentResponse>> getEnrollmentById(@PathVariable Long enrollmentId) {
        StudentEnrollmentResponse response = enrollmentService.getEnrollmentById(enrollmentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/students/{studentId}/active")
    @Operation(summary = "Get active enrollment", description = "Get student's current active enrollment")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'CLASS_TEACHER')")
    public ResponseEntity<ApiResponse<StudentEnrollmentResponse>> getActiveEnrollment(@PathVariable Long studentId) {
        StudentEnrollmentResponse response = enrollmentService.getActiveEnrollment(studentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/students/{studentId}/history")
    @Operation(summary = "Get enrollment history", description = "Get student's enrollment history")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'CLASS_TEACHER')")
    public ResponseEntity<ApiResponse<List<StudentEnrollmentResponse>>> getStudentEnrollmentHistory(
            @PathVariable Long studentId) {

        List<StudentEnrollmentResponse> history = enrollmentService.getStudentEnrollmentHistory(studentId);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @GetMapping("/classes/{classId}")
    @Operation(summary = "Get class enrollments", description = "Get all active enrollments for class")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'CLASS_TEACHER')")
    public ResponseEntity<ApiResponse<PageResponse<StudentEnrollmentResponse>>> getClassEnrollments(
            @PathVariable Long classId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("enrollmentDate").descending());
        PageResponse<StudentEnrollmentResponse> response = enrollmentService.getClassEnrollments(classId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/classes/{classId}/status/{status}")
    @Operation(summary = "Get enrollments by status", description = "Get class enrollments filtered by status")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
    public ResponseEntity<ApiResponse<PageResponse<StudentEnrollmentResponse>>> getEnrollmentsByStatus(
            @PathVariable Long classId,
            @PathVariable StudentEnrollment.EnrollmentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("enrollmentDate").descending());
        PageResponse<StudentEnrollmentResponse> response = enrollmentService.getEnrollmentsByStatus(classId, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get enrollments by date range", description = "Get enrollments within date range")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
    public ResponseEntity<ApiResponse<PageResponse<StudentEnrollmentResponse>>> getEnrollmentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("enrollmentDate").descending());
        PageResponse<StudentEnrollmentResponse> response = enrollmentService.getEnrollmentsByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/sessions/{sessionId}")
    @Operation(summary = "Get session enrollments", description = "Get all active enrollments for session")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
    public ResponseEntity<ApiResponse<PageResponse<StudentEnrollmentResponse>>> getSessionEnrollments(
            @PathVariable Long sessionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("enrollmentDate").descending());
        PageResponse<StudentEnrollmentResponse> response = enrollmentService.getSessionEnrollments(sessionId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{enrollmentId}/complete")
    @Operation(summary = "Complete enrollment", description = "Mark enrollment as completed")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
    public ResponseEntity<ApiResponse<String>> completeEnrollment(@PathVariable Long enrollmentId) {
        enrollmentService.completeEnrollment(enrollmentId);
        return ResponseEntity.ok(ApiResponse.success("Enrollment completed successfully"));
    }

    @PutMapping("/{enrollmentId}/transfer")
    @Operation(summary = "Transfer enrollment", description = "Mark enrollment as transferred")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
    public ResponseEntity<ApiResponse<String>> transferEnrollment(@PathVariable Long enrollmentId) {
        enrollmentService.transferEnrollment(enrollmentId);
        return ResponseEntity.ok(ApiResponse.success("Enrollment transferred successfully"));
    }

    @GetMapping("/classes/{classId}/count")
    @Operation(summary = "Get enrollment count", description = "Get active enrollment count for class")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'CLASS_TEACHER')")
    public ResponseEntity<ApiResponse<Long>> getClassEnrollmentCount(@PathVariable Long classId) {
        long count = enrollmentService.getClassEnrollmentCount(classId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/students/{studentId}/classes/{classId}/exists")
    @Operation(summary = "Check enrollment exists", description = "Check if student is enrolled in class")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'CLASS_TEACHER')")
    public ResponseEntity<ApiResponse<Boolean>> isStudentEnrolledInClass(
            @PathVariable Long studentId,
            @PathVariable Long classId) {

        boolean isEnrolled = enrollmentService.isStudentEnrolledInClass(studentId, classId);
        return ResponseEntity.ok(ApiResponse.success(isEnrolled));
    }
}
