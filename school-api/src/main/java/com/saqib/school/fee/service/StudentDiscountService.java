package com.saqib.school.fee.service;

import com.saqib.school.common.audit.Auditable;
import com.saqib.school.common.dto.PageResponse;
import com.saqib.school.common.exception.BadRequestException;
import com.saqib.school.common.exception.ResourceNotFoundException;
import com.saqib.school.fee.entity.FeeCategory;
import com.saqib.school.fee.entity.StudentDiscount;
import com.saqib.school.fee.mapper.StudentDiscountMapper;
import com.saqib.school.fee.model.StudentDiscountRequest;
import com.saqib.school.fee.model.StudentDiscountResponse;
import com.saqib.school.fee.model.StudentDiscountUpdateRequest;
import com.saqib.school.fee.repository.FeeCategoryRepository;
import com.saqib.school.fee.repository.StudentDiscountRepository;
import com.saqib.school.student.entity.Student;
import com.saqib.school.student.repository.StudentRepository;
import com.saqib.school.user.entity.User;
import com.saqib.school.user.repository.UserRepository;
import com.saqib.school.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentDiscountService {

    private final StudentDiscountRepository studentDiscountRepository;
    private final StudentRepository studentRepository;
    private final FeeCategoryRepository feeCategoryRepository;
    private final UserService userService;
    private final StudentDiscountMapper studentDiscountMapper;

    @Transactional
    @Auditable(action = "CREATE_STUDENT_DISCOUNT", entityType = "StudentDiscount")
    public StudentDiscountResponse createStudentDiscount(StudentDiscountRequest request) {
        validateDiscountRequest(request);

        var student = findStudentById(request.getStudentId());
        FeeCategory feeCategory = findFeeCategoryById(request.getFeeCategoryId());
        var currentUser = userService.getCurrentUser();

        var studentDiscount = studentDiscountMapper.toEntity(request);
        studentDiscount.setCreatedBy(currentUser);

        var savedDiscount = studentDiscountRepository.save(studentDiscount);
        log.info("Student discount created for student {} in category {}",
                 student.getRegistrationNumber(), feeCategory.getCategoryName());

        return studentDiscountMapper.toResponse(savedDiscount);
    }

    @Transactional(readOnly = true)
    public StudentDiscountResponse getStudentDiscountById(Long id) {
        var studentDiscount = findStudentDiscountById(id);
        return studentDiscountMapper.toResponse(studentDiscount);
    }

    @Transactional(readOnly = true)
    public List<StudentDiscountResponse> getStudentDiscounts(Long studentId) {
        return studentDiscountRepository.findByStudentIdAndActive(studentId)
            .stream()
            .map(studentDiscountMapper::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<StudentDiscountResponse> getDiscountsByCategory(Long categoryId, Pageable pageable) {
        var page = studentDiscountRepository.findByFeeCategoryIdAndActive(categoryId, pageable)
            .map(studentDiscountMapper::toResponse);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateDiscountAmount(Long studentId, Long categoryId, BigDecimal originalAmount, LocalDate date) {
        return studentDiscountRepository.findValidDiscountByStudentAndCategory(studentId, categoryId, date)
            .map(discount -> discount.calculateDiscount(originalAmount))
            .orElse(BigDecimal.ZERO);
    }

    @Transactional(readOnly = true)
    public StudentDiscountResponse getValidDiscount(Long studentId, Long categoryId, LocalDate date) {
        return studentDiscountRepository.findValidDiscountByStudentAndCategory(studentId, categoryId, date)
            .map(studentDiscountMapper::toResponse)
            .orElse(null);
    }

    @Transactional
    @Auditable(action = "UPDATE_STUDENT_DISCOUNT", entityType = "StudentDiscount")
    public StudentDiscountResponse updateStudentDiscount(Long id, StudentDiscountUpdateRequest request) {
        var studentDiscount = findStudentDiscountById(id);

        if (request.getDiscountType() != null || request.getDiscountValue() != null) {
            validateDiscountValue(
                request.getDiscountType() != null ? request.getDiscountType() : studentDiscount.getDiscountType(),
                request.getDiscountValue() != null ? request.getDiscountValue() : studentDiscount.getDiscountValue()
            );
        }

        studentDiscountMapper.updateEntity(request, studentDiscount);
        studentDiscount = studentDiscountRepository.save(studentDiscount);

        log.info("Student discount updated for student {} in category {}",
                 studentDiscount.getStudent().getRegistrationNumber(),
                 studentDiscount.getFeeCategory().getCategoryName());

        return studentDiscountMapper.toResponse(studentDiscount);
    }

    @Transactional
    @Auditable(action = "TOGGLE_STUDENT_DISCOUNT_STATUS", entityType = "StudentDiscount")
    public void toggleStudentDiscountStatus(Long id) {
        var studentDiscount = findStudentDiscountById(id);
        studentDiscount.setIsActive(!studentDiscount.getIsActive());
        studentDiscountRepository.save(studentDiscount);

        log.info("Student discount status toggled to {} for student {} in category {}",
                 studentDiscount.getIsActive(),
                 studentDiscount.getStudent().getRegistrationNumber(),
                 studentDiscount.getFeeCategory().getCategoryName());
    }

    @Transactional
    @Auditable(action = "DELETE_STUDENT_DISCOUNT", entityType = "StudentDiscount")
    public void deleteStudentDiscount(Long id) {
        var studentDiscount = findStudentDiscountById(id);
        studentDiscountRepository.delete(studentDiscount);

        log.info("Student discount deleted for student {} in category {}",
                 studentDiscount.getStudent().getRegistrationNumber(),
                 studentDiscount.getFeeCategory().getCategoryName());
    }

    @Transactional
    @Auditable(action = "EXPIRE_OLD_DISCOUNTS", entityType = "StudentDiscount")
    public void expireOldDiscounts() {
        List<StudentDiscount> expiredDiscounts = studentDiscountRepository.findExpiredDiscounts(LocalDate.now());

        for (StudentDiscount discount : expiredDiscounts) {
            discount.setIsActive(false);
            studentDiscountRepository.save(discount);
        }

        log.info("Expired {} old discounts", expiredDiscounts.size());
    }

    private StudentDiscount findStudentDiscountById(Long id) {
        return studentDiscountRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student Discount", "id", id));
    }

    private Student findStudentById(Long id) {
        return studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
    }

    private FeeCategory findFeeCategoryById(Long id) {
        return feeCategoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Fee Category", "id", id));
    }

    private void validateDiscountRequest(StudentDiscountRequest request) {
        // Validate discount value
        validateDiscountValue(request.getDiscountType(), request.getDiscountValue());

        // Validate date range
        if (request.getValidTo() != null && request.getValidFrom().isAfter(request.getValidTo())) {
            throw new BadRequestException("Valid from date cannot be after valid to date");
        }

        // Check for overlapping discounts
        boolean hasOverlapping = studentDiscountRepository.findValidDiscountByStudentAndCategory(
            request.getStudentId(), request.getFeeCategoryId(), request.getValidFrom()).isPresent();

        if (hasOverlapping) {
            throw new BadRequestException("Student already has an active discount for this category during the specified period");
        }
    }

    private void validateDiscountValue(StudentDiscount.DiscountType discountType, BigDecimal discountValue) {
        if (discountType == StudentDiscount.DiscountType.PERCENTAGE) {
            if (discountValue.compareTo(BigDecimal.ZERO) <= 0 || discountValue.compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new BadRequestException("Percentage discount must be between 0 and 100");
            }
        } else if (discountType == StudentDiscount.DiscountType.FIXED_AMOUNT) {
            if (discountValue.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Fixed amount discount must be greater than 0");
            }
        }
    }
}
