package com.saqib.school.student.controller;

import com.saqib.school.common.dto.ApiResponse;
import com.saqib.school.common.dto.PageResponse;
import com.saqib.school.student.entity.Student;
import com.saqib.school.student.model.StudentRequest;
import com.saqib.school.student.model.StudentResponse;
import com.saqib.school.student.model.StudentUpdateRequest;
import com.saqib.school.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Student Management", description = "Student CRUD operations and management")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @Operation(summary = "Create student", description = "Create a new student and enroll in specified class")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(@Valid @RequestBody StudentRequest request) {
        StudentResponse response = studentService.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Student created successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get student by ID", description = "Retrieve student details by ID")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'CLASS_TEACHER', 'SUBJECT_TEACHER')")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentById(@PathVariable Long id) {
        StudentResponse response = studentService.getStudentById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/registration/{registrationNumber}")
    @Operation(summary = "Get student by registration number", description = "Retrieve student details by registration number")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'CLASS_TEACHER', 'SUBJECT_TEACHER')")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentByRegistrationNumber(
            @PathVariable String registrationNumber) {
        StudentResponse response = studentService.getStudentByRegistrationNumber(registrationNumber);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Get all students", description = "Retrieve paginated list of all students")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
    public ResponseEntity<ApiResponse<PageResponse<StudentResponse>>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PageResponse<StudentResponse> response = studentService.getAllStudents(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get students by status", description = "Retrieve students filtered by status")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
    public ResponseEntity<ApiResponse<PageResponse<StudentResponse>>> getStudentsByStatus(
            @PathVariable Student.StudentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        PageResponse<StudentResponse> response = studentService.getStudentsByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    @Operation(summary = "Search students", description = "Search students by name or registration number")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'CLASS_TEACHER', 'SUBJECT_TEACHER')")
    public ResponseEntity<ApiResponse<PageResponse<StudentResponse>>> searchStudents(
            @RequestParam @Parameter(description = "Search term for name or registration number") String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        PageResponse<StudentResponse> response = studentService.searchStudents(searchTerm, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/class/{classId}")
    @Operation(summary = "Get students by class", description = "Retrieve students enrolled in specified class")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'CLASS_TEACHER', 'SUBJECT_TEACHER')")
    public ResponseEntity<ApiResponse<PageResponse<StudentResponse>>> getStudentsByClass(
            @PathVariable Long classId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        PageResponse<StudentResponse> response = studentService.getStudentsByClass(classId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/admission-date-range")
    @Operation(summary = "Get students by admission date range", description = "Retrieve students admitted within date range")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
    public ResponseEntity<ApiResponse<PageResponse<StudentResponse>>> getStudentsByAdmissionDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("admissionDate").descending());
        PageResponse<StudentResponse> response = studentService.getStudentsByAdmissionDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update student", description = "Update student information")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentUpdateRequest request) {

        StudentResponse response = studentService.updateStudent(id, request);
        return ResponseEntity.ok(ApiResponse.success("Student updated successfully", response));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update student status", description = "Update student status")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
    public ResponseEntity<ApiResponse<String>> updateStudentStatus(
            @PathVariable Long id,
            @RequestParam Student.StudentStatus status) {

        studentService.updateStudentStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Student status updated successfully"));
    }

    @PostMapping("/{studentId}/transfer")
    @Operation(summary = "Transfer student", description = "Transfer student to different class")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
    public ResponseEntity<ApiResponse<String>> transferStudent(
            @PathVariable Long studentId,
            @RequestParam Long newClassId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate transferDate) {

        studentService.transferStudent(studentId, newClassId, transferDate);
        return ResponseEntity.ok(ApiResponse.success("Student transferred successfully"));
    }

    @GetMapping("/statistics/total-active")
    @Operation(summary = "Get total active students", description = "Get count of all active students")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
    public ResponseEntity<ApiResponse<Long>> getTotalActiveStudents() {
        long count = studentService.getTotalActiveStudents();
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/statistics/class/{classId}/count")
    @Operation(summary = "Get student count in class", description = "Get count of students in specified class")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'CLASS_TEACHER')")
    public ResponseEntity<ApiResponse<Long>> getStudentCountInClass(@PathVariable Long classId) {
        long count = studentService.getStudentCountInClass(classId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/birthdays/today")
    @Operation(summary = "Get today's birthdays", description = "Get students with birthday today")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'CLASS_TEACHER')")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getStudentsBirthdayToday() {
        List<StudentResponse> students = studentService.getStudentsBirthdayToday();
        return ResponseEntity.ok(ApiResponse.success(students));
    }

    @GetMapping("/duplicates")
    @Operation(summary = "Find duplicate students", description = "Find students with same phone or email")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> findDuplicateStudents(
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email) {

        List<StudentResponse> duplicates = studentService.findDuplicateStudents(phone, email);
        return ResponseEntity.ok(ApiResponse.success(duplicates));
    }
}
