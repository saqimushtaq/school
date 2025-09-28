package com.saqib.school.academic.controller;

import com.saqib.school.academic.model.ClassSubjectRequest;
import com.saqib.school.academic.model.ClassSubjectResponse;
import com.saqib.school.academic.service.ClassSubjectService;
import com.saqib.school.common.dto.ApiResponse;
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
@RequestMapping("/api/academic/class-subjects")
@RequiredArgsConstructor
@Tag(name = "Class-Subject Management", description = "Subject assignment to classes")
public class ClassSubjectController {

    private final ClassSubjectService classSubjectService;

    @PostMapping
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Assign subject to class")
    public ResponseEntity<ApiResponse<ClassSubjectResponse>> assignSubjectToClass(@Valid @RequestBody ClassSubjectRequest request) {
        ClassSubjectResponse response = classSubjectService.assignSubjectToClass(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subject assigned to class successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'ACCOUNTANT', 'CLASS_TEACHER', 'SUBJECT_TEACHER')")
    @Operation(summary = "Get class-subject mapping by ID")
    public ResponseEntity<ApiResponse<ClassSubjectResponse>> getClassSubjectById(@PathVariable Long id) {
        ClassSubjectResponse response = classSubjectService.getClassSubjectById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'ACCOUNTANT', 'CLASS_TEACHER', 'SUBJECT_TEACHER')")
    @Operation(summary = "Get subjects by class")
    public ResponseEntity<ApiResponse<List<ClassSubjectResponse>>> getSubjectsByClass(@PathVariable Long classId) {
        List<ClassSubjectResponse> response = classSubjectService.getSubjectsByClassId(classId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/subject/{subjectId}")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'ACCOUNTANT', 'SUBJECT_TEACHER')")
    @Operation(summary = "Get classes by subject")
    public ResponseEntity<ApiResponse<List<ClassSubjectResponse>>> getClassesBySubject(@PathVariable Long subjectId) {
        List<ClassSubjectResponse> response = classSubjectService.getClassesBySubjectId(subjectId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Update class-subject mapping")
    public ResponseEntity<ApiResponse<ClassSubjectResponse>> updateClassSubject(
            @PathVariable Long id,
            @Valid @RequestBody ClassSubjectRequest request) {

        ClassSubjectResponse response = classSubjectService.updateClassSubject(id, request);
        return ResponseEntity.ok(ApiResponse.success("Class-subject mapping updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Remove subject from class")
    public ResponseEntity<ApiResponse<String>> removeSubjectFromClass(@PathVariable Long id) {
        classSubjectService.removeSubjectFromClass(id);
        return ResponseEntity.ok(ApiResponse.success("Subject removed from class successfully"));
    }

    @PostMapping("/class/{classId}/bulk-assign")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Bulk assign subjects to class")
    public ResponseEntity<ApiResponse<List<ClassSubjectResponse>>> bulkAssignSubjectsToClass(
            @PathVariable Long classId,
            @RequestBody List<Long> subjectIds) {

        List<ClassSubjectResponse> response = classSubjectService.bulkAssignSubjectsToClass(classId, subjectIds);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subjects assigned to class successfully", response));
    }

    @PostMapping("/copy-subjects")
    @PreAuthorize("hasRole('PRINCIPAL') or hasRole('ADMIN_OFFICER')")
    @Operation(summary = "Copy subjects from one class to another")
    public ResponseEntity<ApiResponse<List<ClassSubjectResponse>>> copySubjectsFromClass(
            @RequestParam Long sourceClassId,
            @RequestParam Long targetClassId) {

        List<ClassSubjectResponse> response = classSubjectService.copySubjectsFromClass(sourceClassId, targetClassId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subjects copied successfully", response));
    }
}
