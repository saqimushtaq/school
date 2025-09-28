package com.saqib.school.student.controller;

import com.saqib.school.common.dto.ApiResponse;
import com.saqib.school.common.dto.PageResponse;
import com.saqib.school.student.entity.StudentInquiry;
import com.saqib.school.student.model.StudentInquiryRequest;
import com.saqib.school.student.model.StudentInquiryResponse;
import com.saqib.school.student.model.StudentInquiryUpdateRequest;
import com.saqib.school.student.service.StudentInquiryService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
@Tag(name = "Student Inquiry Management", description = "Student inquiry and admission operations")
public class StudentInquiryController {

    private final StudentInquiryService inquiryService;

    @PostMapping
    @Operation(summary = "Create inquiry", description = "Create new student admission inquiry")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'RECEPTION')")
    public ResponseEntity<ApiResponse<StudentInquiryResponse>> createInquiry(
            @Valid @RequestBody StudentInquiryRequest request) {

        StudentInquiryResponse response = inquiryService.createInquiry(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Inquiry created successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get inquiry by ID", description = "Get inquiry details by ID")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'RECEPTION')")
    public ResponseEntity<ApiResponse<StudentInquiryResponse>> getInquiryById(@PathVariable Long id) {
        StudentInquiryResponse response = inquiryService.getInquiryById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Get all inquiries", description = "Get paginated list of all inquiries")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
    public ResponseEntity<ApiResponse<PageResponse<StudentInquiryResponse>>> getAllInquiries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "inquiryDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PageResponse<StudentInquiryResponse> response = inquiryService.getAllInquiries(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get inquiries by status", description = "Get inquiries filtered by status")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'RECEPTION')")
    public ResponseEntity<ApiResponse<PageResponse<StudentInquiryResponse>>> getInquiriesByStatus(
            @PathVariable StudentInquiry.InquiryStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("inquiryDate").descending());
        PageResponse<StudentInquiryResponse> response = inquiryService.getInquiriesByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/source/{source}")
    @Operation(summary = "Get inquiries by source", description = "Get inquiries filtered by source")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
    public ResponseEntity<ApiResponse<PageResponse<StudentInquiryResponse>>> getInquiriesBySource(
            @PathVariable StudentInquiry.InquirySource source,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("inquiryDate").descending());
        PageResponse<StudentInquiryResponse> response = inquiryService.getInquiriesBySource(source, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

  @GetMapping("/date-range")
  @Operation(summary = "Get inquiries by date range", description = "Get inquiries within date range")
  @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
  public ResponseEntity<ApiResponse<PageResponse<StudentInquiryResponse>>> getInquiriesByDateRange(
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("inquiryDate").descending());
    PageResponse<StudentInquiryResponse> response = inquiryService.getInquiriesByDateRange(startDate, endDate, pageable);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/search")
  @Operation(summary = "Search inquiries", description = "Search inquiries by student name, parent name, or phone")
  @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'RECEPTION')")
  public ResponseEntity<ApiResponse<PageResponse<StudentInquiryResponse>>> searchInquiries(
    @RequestParam @Parameter(description = "Search term for student/parent name or phone") String searchTerm,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("inquiryDate").descending());
    PageResponse<StudentInquiryResponse> response = inquiryService.searchInquiries(searchTerm, pageable);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/class/{className}")
  @Operation(summary = "Get inquiries by class", description = "Get inquiries for specific class")
  @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
  public ResponseEntity<ApiResponse<PageResponse<StudentInquiryResponse>>> getInquiriesByClass(
    @PathVariable String className,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("inquiryDate").descending());
    PageResponse<StudentInquiryResponse> response = inquiryService.getInquiriesByClass(className, pageable);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/my-inquiries")
  @Operation(summary = "Get my inquiries", description = "Get inquiries created by current user")
  @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'RECEPTION')")
  public ResponseEntity<ApiResponse<PageResponse<StudentInquiryResponse>>> getMyInquiries(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("inquiryDate").descending());
    PageResponse<StudentInquiryResponse> response = inquiryService.getMyInquiries(pageable);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/follow-up")
  @Operation(summary = "Get follow-up inquiries", description = "Get inquiries due for follow-up")
  @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'RECEPTION')")
  public ResponseEntity<ApiResponse<List<StudentInquiryResponse>>> getInquiriesDueForFollowUp() {
    List<StudentInquiryResponse> inquiries = inquiryService.getInquiriesDueForFollowUp();
    return ResponseEntity.ok(ApiResponse.success(inquiries));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update inquiry", description = "Update inquiry information")
  @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'RECEPTION')")
  public ResponseEntity<ApiResponse<StudentInquiryResponse>> updateInquiry(
    @PathVariable Long id,
    @Valid @RequestBody StudentInquiryUpdateRequest request) {

    StudentInquiryResponse response = inquiryService.updateInquiry(id, request);
    return ResponseEntity.ok(ApiResponse.success("Inquiry updated successfully", response));
  }

  @PutMapping("/{id}/status")
  @Operation(summary = "Update inquiry status", description = "Update inquiry status")
  @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'RECEPTION')")
  public ResponseEntity<ApiResponse<String>> updateInquiryStatus(
    @PathVariable Long id,
    @RequestParam StudentInquiry.InquiryStatus status) {

    inquiryService.updateInquiryStatus(id, status);
    return ResponseEntity.ok(ApiResponse.success("Inquiry status updated successfully"));
  }

  @PostMapping("/{id}/contact")
  @Operation(summary = "Mark as contacted", description = "Mark inquiry as contacted with notes")
  @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'RECEPTION')")
  public ResponseEntity<ApiResponse<String>> markAsContacted(
    @PathVariable Long id,
    @RequestParam(required = false) String notes) {

    inquiryService.markAsContacted(id, notes);
    return ResponseEntity.ok(ApiResponse.success("Inquiry marked as contacted"));
  }

  @PostMapping("/{id}/interested")
  @Operation(summary = "Mark as interested", description = "Mark inquiry as interested with follow-up date")
  @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'RECEPTION')")
  public ResponseEntity<ApiResponse<String>> markAsInterested(
    @PathVariable Long id,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate followUpDate) {

    inquiryService.markAsInterested(id, followUpDate);
    return ResponseEntity.ok(ApiResponse.success("Inquiry marked as interested"));
  }

  @PostMapping("/{id}/admit")
  @Operation(summary = "Mark as admitted", description = "Mark inquiry as admitted")
  @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
  public ResponseEntity<ApiResponse<String>> markAsAdmitted(@PathVariable Long id) {
    inquiryService.markAsAdmitted(id);
    return ResponseEntity.ok(ApiResponse.success("Inquiry marked as admitted"));
  }

  @PostMapping("/{id}/reject")
  @Operation(summary = "Mark as rejected", description = "Mark inquiry as rejected with reason")
  @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
  public ResponseEntity<ApiResponse<String>> markAsRejected(
    @PathVariable Long id,
    @RequestParam(required = false) String reason) {

    inquiryService.markAsRejected(id, reason);
    return ResponseEntity.ok(ApiResponse.success("Inquiry marked as rejected"));
  }

  @PutMapping("/{id}/registration-fee")
  @Operation(summary = "Update registration fee status", description = "Update registration fee payment status")
  @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER', 'ACCOUNTANT')")
  public ResponseEntity<ApiResponse<String>> updateRegistrationFeeStatus(
    @PathVariable Long id,
    @RequestParam boolean paid) {

    inquiryService.updateRegistrationFeeStatus(id, paid);
    return ResponseEntity.ok(ApiResponse.success("Registration fee status updated"));
  }

  @GetMapping("/duplicates")
  @Operation(summary = "Find duplicate inquiries", description = "Find inquiries with same phone or email")
  @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
  public ResponseEntity<ApiResponse<List<StudentInquiryResponse>>> findDuplicateInquiries(
    @RequestParam(required = false) String phone,
    @RequestParam(required = false) String email) {

    List<StudentInquiryResponse> duplicates = inquiryService.findDuplicateInquiries(phone, email);
    return ResponseEntity.ok(ApiResponse.success(duplicates));
  }

  @GetMapping("/statistics/status")
  @Operation(summary = "Get inquiry status statistics", description = "Get count of inquiries by status")
  @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
  public ResponseEntity<ApiResponse<Map<StudentInquiry.InquiryStatus, Long>>> getInquiryStatistics() {
    Map<StudentInquiry.InquiryStatus, Long> statistics = inquiryService.getInquiryStatistics();
    return ResponseEntity.ok(ApiResponse.success(statistics));
  }

  @GetMapping("/statistics/source")
  @Operation(summary = "Get inquiry source statistics", description = "Get count of inquiries by source")
  @PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN_OFFICER')")
  public ResponseEntity<ApiResponse<Map<StudentInquiry.InquirySource, Long>>> getInquirySourceStatistics() {
    Map<StudentInquiry.InquirySource, Long> statistics = inquiryService.getInquirySourceStatistics();
    return ResponseEntity.ok(ApiResponse.success(statistics));
  }
}
