package com.saqib.school.fee.service;

import com.saqib.school.common.audit.Auditable;
import com.saqib.school.common.dto.PageResponse;
import com.saqib.school.common.exception.BadRequestException;
import com.saqib.school.common.exception.ResourceNotFoundException;
import com.saqib.school.fee.entity.*;
import com.saqib.school.fee.mapper.FeeVoucherMapper;
import com.saqib.school.fee.model.*;
import com.saqib.school.fee.repository.*;
import com.saqib.school.student.entity.Student;
import com.saqib.school.student.entity.StudentEnrollment;
import com.saqib.school.student.repository.StudentRepository;
import com.saqib.school.user.entity.User;
import com.saqib.school.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeeVoucherService {

    private final FeeVoucherRepository feeVoucherRepository;
    private final FeeVoucherDetailRepository feeVoucherDetailRepository;
    private final StudentRepository studentRepository;
    private final FeeCategoryRepository feeCategoryRepository;
    private final FeeStructureRepository feeStructureRepository;
    private final UserRepository userRepository;
    private final FeeVoucherMapper feeVoucherMapper;
    private final StudentDiscountService studentDiscountService;

    @Transactional
    @Auditable(action = "CREATE_FEE_VOUCHER", entityType = "FeeVoucher")
    public FeeVoucherResponse createFeeVoucher(FeeVoucherRequest request) {
        Student student = findStudentById(request.getStudentId());
        User currentUser = getCurrentUser();

        FeeVoucher feeVoucher = feeVoucherMapper.toEntity(request);
        feeVoucher.setStudent(student);
        feeVoucher.setCreatedBy(currentUser);
        feeVoucher.setVoucherNumber(generateVoucherNumber(request.getVoucherType()));
        feeVoucher.setStatus(FeeVoucher.VoucherStatus.PENDING);

        // Calculate total amount and create voucher details
        BigDecimal totalAmount = BigDecimal.ZERO;
        Set<FeeVoucherDetail> voucherDetails = request.getVoucherDetails().stream()
            .map(detailRequest -> {
                FeeCategory feeCategory = findFeeCategoryById(detailRequest.getFeeCategoryId());

                BigDecimal discountAmount = detailRequest.getDiscountAmount();
                if (discountAmount.equals(BigDecimal.ZERO)) {
                    // Auto-calculate discount if not provided
                    discountAmount = studentDiscountService.calculateDiscountAmount(
                        student.getId(), feeCategory.getId(), detailRequest.getOriginalAmount(), LocalDate.now());
                }

                FeeVoucherDetail detail = new FeeVoucherDetail();
                detail.setVoucher(feeVoucher);
                detail.setFeeCategory(feeCategory);
                detail.setOriginalAmount(detailRequest.getOriginalAmount());
                detail.setDiscountAmount(discountAmount);
                detail.setFinalAmount(detailRequest.getOriginalAmount().subtract(discountAmount));

                return detail;
            })
            .collect(Collectors.toSet());

        totalAmount = voucherDetails.stream()
            .map(FeeVoucherDetail::getFinalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        feeVoucher.setTotalAmount(totalAmount);
        feeVoucher.setVoucherDetails(voucherDetails);

        FeeVoucher savedVoucher = feeVoucherRepository.save(feeVoucher);
        log.info("Fee voucher created: {} for student {}",
                 savedVoucher.getVoucherNumber(), student.getRegistrationNumber());

        return feeVoucherMapper.toResponse(savedVoucher);
    }

    @Transactional
    @Auditable(action = "GENERATE_MONTHLY_VOUCHERS", entityType = "FeeVoucher")
    public List<FeeVoucherResponse> generateMonthlyVouchers(MonthlyVoucherGenerationRequest request) {
        List<Student> students = getStudentsForVoucherGeneration(request);
        List<FeeVoucher> generatedVouchers = students.stream()
            .map(student -> createMonthlyVoucherForStudent(student, request))
            .toList();

        log.info("Generated {} monthly vouchers for {}", generatedVouchers.size(), request.getMonthYear());
        return generatedVouchers.stream()
            .map(feeVoucherMapper::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public FeeVoucherResponse getFeeVoucherById(Long id) {
        FeeVoucher feeVoucher = findFeeVoucherById(id);
        return feeVoucherMapper.toResponse(feeVoucher);
    }

    @Transactional(readOnly = true)
    public FeeVoucherResponse getFeeVoucherByNumber(String voucherNumber) {
        FeeVoucher feeVoucher = feeVoucherRepository.findByVoucherNumber(voucherNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Fee Voucher", "voucherNumber", voucherNumber));
        return feeVoucherMapper.toResponse(feeVoucher);
    }

    @Transactional(readOnly = true)
    public PageResponse<FeeVoucherResponse> getStudentVouchers(Long studentId, Pageable pageable) {
        var page = feeVoucherRepository.findByStudentId(studentId, pageable)
            .map(feeVoucherMapper::toResponse);
      return PageResponse.from(page);
    }

  @Transactional(readOnly = true)
  public PageResponse<FeeVoucherResponse> getVouchersByStatus(FeeVoucher.VoucherStatus status, Pageable pageable) {
    var page = feeVoucherRepository.findByStatus(status, pageable)
      .map(feeVoucherMapper::toResponse);
    return PageResponse.from(page);
  }

  @Transactional(readOnly = true)
  public PageResponse<FeeVoucherResponse> getVouchersByType(FeeVoucher.VoucherType type, Pageable pageable) {
    var page = feeVoucherRepository.findByVoucherType(type, pageable)
      .map(feeVoucherMapper::toResponse);
    return PageResponse.from(page);
  }

  @Transactional(readOnly = true)
  public PageResponse<FeeVoucherResponse> getVouchersByMonthYear(String monthYear, Pageable pageable) {
    var page = feeVoucherRepository.findByMonthYear(monthYear, pageable)
      .map(feeVoucherMapper::toResponse);
    return PageResponse.from(page);
  }

  @Transactional(readOnly = true)
  public PageResponse<FeeVoucherResponse> getVouchersByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
    var page = feeVoucherRepository.findByIssueDateBetween(startDate, endDate, pageable)
      .map(feeVoucherMapper::toResponse);
    return PageResponse.from(page);
  }

  @Transactional
  @Auditable(action = "CANCEL_FEE_VOUCHER", entityType = "FeeVoucher")
  public void cancelFeeVoucher(Long id, String reason) {
    FeeVoucher feeVoucher = findFeeVoucherById(id);

    if (feeVoucher.isPaid()) {
      throw new BadRequestException("Cannot cancel a paid voucher");
    }

    feeVoucher.cancel();
    feeVoucher.setNotes((feeVoucher.getNotes() != null ? feeVoucher.getNotes() + "; " : "") +
      "Cancelled: " + reason);

    feeVoucherRepository.save(feeVoucher);
    log.info("Fee voucher cancelled: {} - Reason: {}", feeVoucher.getVoucherNumber(), reason);
  }

  @Transactional
  @Auditable(action = "PROCESS_OVERDUE_VOUCHERS", entityType = "FeeVoucher")
  public void processOverdueVouchers() {
    List<FeeVoucher> overdueVouchers = feeVoucherRepository.findOverdueVouchers(LocalDate.now());

    for (FeeVoucher voucher : overdueVouchers) {
      voucher.markAsOverdue();
      feeVoucherRepository.save(voucher);
    }

    log.info("Processed {} overdue vouchers", overdueVouchers.size());
  }

  @Transactional(readOnly = true)
  public long getPendingVouchersCount() {
    return feeVoucherRepository.countPendingVouchers();
  }

  @Transactional(readOnly = true)
  public BigDecimal getTotalCollectionForPeriod(LocalDate startDate, LocalDate endDate) {
    BigDecimal total = feeVoucherRepository.sumPaidAmountBetweenDates(startDate, endDate);
    return total != null ? total : BigDecimal.ZERO;
  }

  private FeeVoucher createMonthlyVoucherForStudent(Student student, MonthlyVoucherGenerationRequest request) {
    // Get current active enrollment
    StudentEnrollment activeEnrollment = student.getEnrollments().stream()
      .filter(enrollment -> enrollment.getStatus() == StudentEnrollment.EnrollmentStatus.ACTIVE)
      .findFirst()
      .orElseThrow(() -> new BadRequestException("Student " + student.getRegistrationNumber() + " has no active enrollment"));

    // Get fee structures for the student's class
    List<FeeStructure> feeStructures = feeStructureRepository.findByClassIdAndActive(activeEnrollment.getSchoolClass().getId());

    if (feeStructures.isEmpty()) {
      log.warn("No fee structures found for class {}", activeEnrollment.getSchoolClass().getDisplayName());
      return null;
    }

    // Create voucher
    FeeVoucher voucher = FeeVoucher.builder()
      .voucherNumber(generateVoucherNumber(FeeVoucher.VoucherType.MONTHLY))
      .student(student)
      .voucherType(FeeVoucher.VoucherType.MONTHLY)
      .monthYear(request.getMonthYear())
      .issueDate(LocalDate.now())
      .dueDate(request.getDueDate())
      .status(FeeVoucher.VoucherStatus.PENDING)
      .createdBy(getCurrentUser())
      .build();

    // Create voucher details
    Set<FeeVoucherDetail> details = feeStructures.stream()
      .filter(fs -> fs.getIsMonthly())
      .map(feeStructure -> {
        BigDecimal discountAmount = studentDiscountService.calculateDiscountAmount(
          student.getId(), feeStructure.getFeeCategory().getId(), feeStructure.getAmount(), LocalDate.now());

        return FeeVoucherDetail.builder()
          .voucher(voucher)
          .feeCategory(feeStructure.getFeeCategory())
          .originalAmount(feeStructure.getAmount())
          .discountAmount(discountAmount)
          .finalAmount(feeStructure.getAmount().subtract(discountAmount))
          .build();
      })
      .collect(Collectors.toSet());

    BigDecimal totalAmount = details.stream()
      .map(FeeVoucherDetail::getFinalAmount)
      .reduce(BigDecimal.ZERO, BigDecimal::add);

    voucher.setTotalAmount(totalAmount);
    voucher.setVoucherDetails(details);

    return feeVoucherRepository.save(voucher);
  }

  private List<Student> getStudentsForVoucherGeneration(MonthlyVoucherGenerationRequest request) {
    if (request.getStudentIds() != null && !request.getStudentIds().isEmpty()) {
      return studentRepository.findAllById(request.getStudentIds());
    }

    if (request.getClassIds() != null && !request.getClassIds().isEmpty()) {
      return request.getClassIds().stream()
        .flatMap(classId -> studentRepository.findByActiveEnrollmentInClass(classId, Pageable.unpaged()).getContent().stream())
        .distinct()
        .toList();
    }

    // Get all active students
    return studentRepository.findByStatus(Student.StudentStatus.ACTIVE, Pageable.unpaged()).getContent();
  }

  private String generateVoucherNumber(FeeVoucher.VoucherType voucherType) {
    String prefix = switch (voucherType) {
      case ADMISSION -> "ADM";
      case MONTHLY -> "MON";
      case INSTALLMENT -> "INS";
    };

    String yearMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMM"));
    long count = feeVoucherRepository.count() + 1;

    return String.format("%s-%s-%05d", prefix, yearMonth, count);
  }

  private FeeVoucher findFeeVoucherById(Long id) {
    return feeVoucherRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Fee Voucher", "id", id));
  }

  private Student findStudentById(Long id) {
    return studentRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
  }

  private FeeCategory findFeeCategoryById(Long id) {
    return feeCategoryRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Fee Category", "id", id));
  }

  private User getCurrentUser() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return userRepository.findByUsername(username)
      .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
  }
}
