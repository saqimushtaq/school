package com.saqib.school.student.service;

import com.saqib.school.common.audit.Auditable;
import com.saqib.school.common.dto.PageResponse;
import com.saqib.school.common.exception.BadRequestException;
import com.saqib.school.common.exception.ResourceNotFoundException;
import com.saqib.school.student.entity.StudentInquiry;
import com.saqib.school.student.mapper.StudentInquiryMapper;
import com.saqib.school.student.model.StudentInquiryRequest;
import com.saqib.school.student.model.StudentInquiryResponse;
import com.saqib.school.student.model.StudentInquiryUpdateRequest;
import com.saqib.school.student.repository.StudentInquiryRepository;
import com.saqib.school.user.entity.User;
import com.saqib.school.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentInquiryService {

    private final StudentInquiryRepository inquiryRepository;
    private final UserRepository userRepository;
    private final StudentInquiryMapper inquiryMapper;

    @Transactional
    @Auditable(action = "CREATE_INQUIRY", entityType = "StudentInquiry")
    public StudentInquiryResponse createInquiry(StudentInquiryRequest request) {
        validateInquiryRequest(request);

        StudentInquiry inquiry = inquiryMapper.toEntity(request);
        inquiry.setStatus(StudentInquiry.InquiryStatus.NEW);
        inquiry.setCreatedBy(getCurrentUser());

        StudentInquiry savedInquiry = inquiryRepository.save(inquiry);

        log.info("New inquiry created for student: {} by parent: {}",
                savedInquiry.getStudentName(), savedInquiry.getParentName());

        return inquiryMapper.toResponse(savedInquiry);
    }

    @Transactional(readOnly = true)
    public StudentInquiryResponse getInquiryById(Long id) {
        StudentInquiry inquiry = findInquiryById(id);
        return inquiryMapper.toResponse(inquiry);
    }

    @Transactional(readOnly = true)
    public PageResponse<StudentInquiryResponse> getAllInquiries(Pageable pageable) {
        var inquiryPage = inquiryRepository.findAll(pageable)
                .map(inquiryMapper::toResponse);
        return PageResponse.from(inquiryPage);
    }

    @Transactional(readOnly = true)
    public PageResponse<StudentInquiryResponse> getInquiriesByStatus(StudentInquiry.InquiryStatus status,
                                                                    Pageable pageable) {
        var inquiryPage = inquiryRepository.findByStatus(status, pageable)
                .map(inquiryMapper::toResponse);
        return PageResponse.from(inquiryPage);
    }

    @Transactional(readOnly = true)
    public PageResponse<StudentInquiryResponse> getInquiriesBySource(StudentInquiry.InquirySource source,
                                                                    Pageable pageable) {
        var inquiryPage = inquiryRepository.findByInquirySource(source, pageable)
                .map(inquiryMapper::toResponse);
        return PageResponse.from(inquiryPage);
    }

    @Transactional(readOnly = true)
    public PageResponse<StudentInquiryResponse> getInquiriesByDateRange(LocalDate startDate,
                                                                       LocalDate endDate,
                                                                       Pageable pageable) {
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date");
        }

        var inquiryPage = inquiryRepository.findByInquiryDateBetween(startDate, endDate, pageable)
                .map(inquiryMapper::toResponse);
        return PageResponse.from(inquiryPage);
    }

    @Transactional(readOnly = true)
    public PageResponse<StudentInquiryResponse> searchInquiries(String searchTerm, Pageable pageable) {
        var inquiryPage = inquiryRepository.findBySearchTerm(searchTerm, pageable)
                .map(inquiryMapper::toResponse);
        return PageResponse.from(inquiryPage);
    }

    @Transactional(readOnly = true)
    public PageResponse<StudentInquiryResponse> getInquiriesByClass(String className, Pageable pageable) {
        var inquiryPage = inquiryRepository.findByInterestedClass(className, pageable)
                .map(inquiryMapper::toResponse);
        return PageResponse.from(inquiryPage);
    }

    @Transactional(readOnly = true)
    public PageResponse<StudentInquiryResponse> getMyInquiries(Pageable pageable) {
        User currentUser = getCurrentUser();
        var inquiryPage = inquiryRepository.findByCreatedBy(currentUser.getId(), pageable)
                .map(inquiryMapper::toResponse);
        return PageResponse.from(inquiryPage);
    }

    @Transactional(readOnly = true)
    public List<StudentInquiryResponse> getInquiriesDueForFollowUp() {
        LocalDate today = LocalDate.now();
        List<StudentInquiry> inquiries = inquiryRepository.findInquiriesDueForFollowUp(today);
        return inquiries.stream()
                .map(inquiryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Auditable(action = "UPDATE_INQUIRY", entityType = "StudentInquiry")
    public StudentInquiryResponse updateInquiry(Long id, StudentInquiryUpdateRequest request) {
        StudentInquiry inquiry = findInquiryById(id);

        validateInquiryUpdateRequest(request, inquiry);

        inquiryMapper.updateEntity(request, inquiry);
        StudentInquiry updatedInquiry = inquiryRepository.save(inquiry);

        log.info("Inquiry updated for student: {} (ID: {})",
                updatedInquiry.getStudentName(), updatedInquiry.getId());

        return inquiryMapper.toResponse(updatedInquiry);
    }

    @Transactional
    @Auditable(action = "UPDATE_INQUIRY_STATUS", entityType = "StudentInquiry")
    public void updateInquiryStatus(Long id, StudentInquiry.InquiryStatus status) {
        StudentInquiry inquiry = findInquiryById(id);

        validateStatusTransition(inquiry.getStatus(), status);

        inquiry.setStatus(status);
        inquiryRepository.save(inquiry);

        log.info("Inquiry status updated to {} for student: {} (ID: {})",
                status, inquiry.getStudentName(), inquiry.getId());
    }

    @Transactional
    @Auditable(action = "MARK_INQUIRY_CONTACTED", entityType = "StudentInquiry")
    public void markAsContacted(Long id, String notes) {
        StudentInquiry inquiry = findInquiryById(id);

        if (!inquiry.canBeContacted()) {
            throw new BadRequestException("Inquiry cannot be marked as contacted in current status: " + inquiry.getStatus());
        }

        inquiry.markAsContacted();
        if (notes != null && !notes.trim().isEmpty()) {
            String existingNotes = inquiry.getNotes() != null ? inquiry.getNotes() : "";
            inquiry.setNotes(existingNotes + "\n[" + LocalDate.now() + "] " + notes);
        }

        inquiryRepository.save(inquiry);

        log.info("Inquiry marked as contacted for student: {} (ID: {})",
                inquiry.getStudentName(), inquiry.getId());
    }

    @Transactional
    @Auditable(action = "MARK_INQUIRY_INTERESTED", entityType = "StudentInquiry")
    public void markAsInterested(Long id, LocalDate followUpDate) {
        StudentInquiry inquiry = findInquiryById(id);

        if (inquiry.getStatus() != StudentInquiry.InquiryStatus.CONTACTED) {
            throw new BadRequestException("Only contacted inquiries can be marked as interested");
        }

        inquiry.markAsInterested();
        if (followUpDate != null) {
            inquiry.setFollowUpDate(followUpDate);
        }

        inquiryRepository.save(inquiry);

        log.info("Inquiry marked as interested for student: {} (ID: {})",
                inquiry.getStudentName(), inquiry.getId());
    }

    @Transactional
    @Auditable(action = "MARK_INQUIRY_ADMITTED", entityType = "StudentInquiry")
    public void markAsAdmitted(Long id) {
        StudentInquiry inquiry = findInquiryById(id);

        if (!inquiry.canBeAdmitted()) {
            throw new BadRequestException("Inquiry cannot be marked as admitted in current status: " + inquiry.getStatus());
        }

        inquiry.markAsAdmitted();
        inquiryRepository.save(inquiry);

        log.info("Inquiry marked as admitted for student: {} (ID: {})",
                inquiry.getStudentName(), inquiry.getId());
    }

    @Transactional
    @Auditable(action = "MARK_INQUIRY_REJECTED", entityType = "StudentInquiry")
    public void markAsRejected(Long id, String reason) {
        StudentInquiry inquiry = findInquiryById(id);

        if (inquiry.getStatus() == StudentInquiry.InquiryStatus.ADMITTED) {
            throw new BadRequestException("Cannot reject an already admitted inquiry");
        }

        inquiry.markAsRejected();
        if (reason != null && !reason.trim().isEmpty()) {
            String existingNotes = inquiry.getNotes() != null ? inquiry.getNotes() : "";
            inquiry.setNotes(existingNotes + "\n[REJECTED - " + LocalDate.now() + "] " + reason);
        }

        inquiryRepository.save(inquiry);

        log.info("Inquiry marked as rejected for student: {} (ID: {})",
                inquiry.getStudentName(), inquiry.getId());
    }

    @Transactional
    @Auditable(action = "UPDATE_REGISTRATION_FEE_STATUS", entityType = "StudentInquiry")
    public void updateRegistrationFeeStatus(Long id, boolean paid) {
        StudentInquiry inquiry = findInquiryById(id);

        inquiry.setRegistrationFeePaid(paid);
        inquiryRepository.save(inquiry);

        log.info("Registration fee status updated to {} for inquiry: {} (ID: {})",
                paid ? "PAID" : "UNPAID", inquiry.getStudentName(), inquiry.getId());
    }

    @Transactional(readOnly = true)
    public List<StudentInquiryResponse> findDuplicateInquiries(String phone, String email) {
        List<StudentInquiry> inquiries = inquiryRepository.findByParentPhoneOrEmail(phone, email);
        return inquiries.stream()
                .map(inquiryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<StudentInquiry.InquiryStatus, Long> getInquiryStatistics() {
        return Map.of(
            StudentInquiry.InquiryStatus.NEW, inquiryRepository.countByStatus(StudentInquiry.InquiryStatus.NEW),
            StudentInquiry.InquiryStatus.CONTACTED, inquiryRepository.countByStatus(StudentInquiry.InquiryStatus.CONTACTED),
            StudentInquiry.InquiryStatus.INTERESTED, inquiryRepository.countByStatus(StudentInquiry.InquiryStatus.INTERESTED),
            StudentInquiry.InquiryStatus.ADMITTED, inquiryRepository.countByStatus(StudentInquiry.InquiryStatus.ADMITTED),
            StudentInquiry.InquiryStatus.REJECTED, inquiryRepository.countByStatus(StudentInquiry.InquiryStatus.REJECTED),
            StudentInquiry.InquiryStatus.LOST, inquiryRepository.countByStatus(StudentInquiry.InquiryStatus.LOST)
        );
    }

    @Transactional(readOnly = true)
    public Map<StudentInquiry.InquirySource, Long> getInquirySourceStatistics() {
        return Map.of(
            StudentInquiry.InquirySource.REFERRAL, inquiryRepository.countByInquirySource(StudentInquiry.InquirySource.REFERRAL),
            StudentInquiry.InquirySource.WALK_IN, inquiryRepository.countByInquirySource(StudentInquiry.InquirySource.WALK_IN),
            StudentInquiry.InquirySource.ADVERTISEMENT, inquiryRepository.countByInquirySource(StudentInquiry.InquirySource.ADVERTISEMENT),
            StudentInquiry.InquirySource.ONLINE, inquiryRepository.countByInquirySource(StudentInquiry.InquirySource.ONLINE),
            StudentInquiry.InquirySource.SOCIAL_MEDIA, inquiryRepository.countByInquirySource(StudentInquiry.InquirySource.SOCIAL_MEDIA),
            StudentInquiry.InquirySource.OTHER, inquiryRepository.countByInquirySource(StudentInquiry.InquirySource.OTHER)
        );
    }

    private StudentInquiry findInquiryById(Long id) {
        return inquiryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inquiry", "id", id));
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    private void validateInquiryRequest(StudentInquiryRequest request) {
        // Check for duplicate inquiries
        if (request.getParentPhone() != null || request.getParentEmail() != null) {
            List<StudentInquiry> duplicates = inquiryRepository.findByParentPhoneOrEmail(
                    request.getParentPhone(), request.getParentEmail());

            // Check if there's an active inquiry (not rejected or admitted)
            boolean hasActiveInquiry = duplicates.stream()
                    .anyMatch(inquiry -> inquiry.getStatus() != StudentInquiry.InquiryStatus.REJECTED &&
                                      inquiry.getStatus() != StudentInquiry.InquiryStatus.ADMITTED);

            if (hasActiveInquiry) {
                throw new BadRequestException("An active inquiry already exists for this parent contact");
            }
        }

        // Validate inquiry date is not in future
        if (request.getInquiryDate().isAfter(LocalDate.now())) {
            throw new BadRequestException("Inquiry date cannot be in the future");
        }

        // Validate follow-up date if provided
        if (request.getFollowUpDate() != null && request.getFollowUpDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Follow-up date cannot be in the past");
        }
    }

    private void validateInquiryUpdateRequest(StudentInquiryUpdateRequest request, StudentInquiry existingInquiry) {
        // Check for duplicate contact information (excluding current inquiry)
        if (request.getParentPhone() != null || request.getParentEmail() != null) {
            List<StudentInquiry> duplicates = inquiryRepository.findByParentPhoneOrEmail(
                    request.getParentPhone(), request.getParentEmail());

            boolean hasDuplicates = duplicates.stream()
                    .anyMatch(inquiry -> !inquiry.getId().equals(existingInquiry.getId()) &&
                                      inquiry.getStatus() != StudentInquiry.InquiryStatus.REJECTED &&
                                      inquiry.getStatus() != StudentInquiry.InquiryStatus.ADMITTED);

            if (hasDuplicates) {
                throw new BadRequestException("An active inquiry already exists for this parent contact");
            }
        }

        // Validate status transition if status is being updated
        if (request.getStatus() != null) {
            validateStatusTransition(existingInquiry.getStatus(), request.getStatus());
        }

        // Validate follow-up date if provided
        if (request.getFollowUpDate() != null && request.getFollowUpDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Follow-up date cannot be in the past");
        }
    }

    private void validateStatusTransition(StudentInquiry.InquiryStatus currentStatus, StudentInquiry.InquiryStatus newStatus) {
        // Define valid status transitions
        boolean isValidTransition = switch (currentStatus) {
            case NEW -> newStatus == StudentInquiry.InquiryStatus.CONTACTED ||
                       newStatus == StudentInquiry.InquiryStatus.REJECTED ||
                       newStatus == StudentInquiry.InquiryStatus.LOST;
            case CONTACTED -> newStatus == StudentInquiry.InquiryStatus.INTERESTED ||
                             newStatus == StudentInquiry.InquiryStatus.REJECTED ||
                             newStatus == StudentInquiry.InquiryStatus.LOST;
            case INTERESTED -> newStatus == StudentInquiry.InquiryStatus.ADMITTED ||
                              newStatus == StudentInquiry.InquiryStatus.REJECTED ||
                              newStatus == StudentInquiry.InquiryStatus.LOST;
            case ADMITTED, REJECTED, LOST -> false; // Final states
        };

        if (!isValidTransition) {
            throw new BadRequestException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }
    }
}
