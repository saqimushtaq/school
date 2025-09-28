package com.saqib.school.student.controller;

import com.saqib.school.common.dto.ApiResponse;
import com.saqib.school.student.model.StudentGuardianRequest;
import com.saqib.school.student.model.StudentGuardianResponse;
import com.saqib.school.student.service.StudentGuardianService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Student Guardian Management", description = "Student guardian operations")
public class StudentGuardianController {

    private final StudentGuardianService guardianService;

    @PostMapping("/{studentId}/guardians")
    @Operation(summary = "Add guardian", description = "Add guardian for student")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
    public ResponseEntity<ApiResponse<StudentGuardianResponse>> addGuardian(
            @PathVariable Long studentId,
            @Valid @RequestBody StudentGuardianRequest request) {

        StudentGuardianResponse response = guardianService.addGuardian(studentId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Guardian added successfully", response));
    }

    @GetMapping("/{studentId}/guardians")
    @Operation(summary = "Get student guardians", description = "Get all guardians for student")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'CLASS_TEACHER')")
    public ResponseEntity<ApiResponse<List<StudentGuardianResponse>>> getStudentGuardians(
            @PathVariable Long studentId) {

        List<StudentGuardianResponse> guardians = guardianService.getStudentGuardians(studentId);
        return ResponseEntity.ok(ApiResponse.success(guardians));
    }

    @GetMapping("/guardians/{guardianId}")
    @Operation(summary = "Get guardian by ID", description = "Get guardian details by ID")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'CLASS_TEACHER')")
    public ResponseEntity<ApiResponse<StudentGuardianResponse>> getGuardianById(@PathVariable Long guardianId) {
        StudentGuardianResponse response = guardianService.getGuardianById(guardianId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{studentId}/guardians/primary")
    @Operation(summary = "Get primary contact", description = "Get primary contact guardian for student")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'CLASS_TEACHER')")
    public ResponseEntity<ApiResponse<StudentGuardianResponse>> getPrimaryContact(@PathVariable Long studentId) {
        StudentGuardianResponse response = guardianService.getPrimaryContact(studentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/guardians/{guardianId}")
    @Operation(summary = "Update guardian", description = "Update guardian information")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
    public ResponseEntity<ApiResponse<StudentGuardianResponse>> updateGuardian(
            @PathVariable Long guardianId,
            @Valid @RequestBody StudentGuardianRequest request) {

        StudentGuardianResponse response = guardianService.updateGuardian(guardianId, request);
        return ResponseEntity.ok(ApiResponse.success("Guardian updated successfully", response));
    }

    @PutMapping("/guardians/{guardianId}/primary")
    @Operation(summary = "Set primary contact", description = "Set guardian as primary contact")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
    public ResponseEntity<ApiResponse<String>> setPrimaryContact(@PathVariable Long guardianId) {
        guardianService.setPrimaryContact(guardianId);
        return ResponseEntity.ok(ApiResponse.success("Primary contact updated successfully"));
    }

    @DeleteMapping("/guardians/{guardianId}")
    @Operation(summary = "Delete guardian", description = "Delete guardian (requires at least one guardian to remain)")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
    public ResponseEntity<ApiResponse<String>> deleteGuardian(@PathVariable Long guardianId) {
        guardianService.deleteGuardian(guardianId);
        return ResponseEntity.ok(ApiResponse.success("Guardian deleted successfully"));
    }

    @GetMapping("/guardians/search")
    @Operation(summary = "Find guardians by contact", description = "Find guardians by phone or email")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
    public ResponseEntity<ApiResponse<List<StudentGuardianResponse>>> findGuardiansByContact(
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email) {

        List<StudentGuardianResponse> guardians = guardianService.findGuardiansByContact(phone, email);
        return ResponseEntity.ok(ApiResponse.success(guardians));
    }

    @GetMapping("/guardians/cnic/{cnic}")
    @Operation(summary = "Find guardians by CNIC", description = "Find guardians by CNIC number")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
    public ResponseEntity<ApiResponse<List<StudentGuardianResponse>>> findGuardiansByCnic(@PathVariable String cnic) {
        List<StudentGuardianResponse> guardians = guardianService.findGuardiansByCnic(cnic);
        return ResponseEntity.ok(ApiResponse.success(guardians));
    }
}
