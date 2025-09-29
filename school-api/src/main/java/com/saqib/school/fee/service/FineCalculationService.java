package com.saqib.school.fee.service;

import com.saqib.school.common.audit.Auditable;
import com.saqib.school.fee.entity.FeeVoucher;
import com.saqib.school.fee.entity.FineStructure;
import com.saqib.school.fee.model.FineCalculationRequest;
import com.saqib.school.fee.repository.FeeVoucherRepository;
import com.saqib.school.fee.repository.FineStructureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FineCalculationService {

    private final FeeVoucherRepository feeVoucherRepository;
    private final FineStructureRepository fineStructureRepository;

    @Transactional
    @Auditable(action = "CALCULATE_FINES", entityType = "FeeVoucher")
    public Map<Long, BigDecimal> calculateFines(FineCalculationRequest request) {
        return request.getVoucherIds().stream()
            .collect(Collectors.toMap(
                voucherId -> voucherId,
                voucherId -> calculateFineForVoucher(voucherId, request.getCalculationDate())
            ));
    }

    @Transactional
    @Auditable(action = "APPLY_CALCULATED_FINES", entityType = "FeeVoucher")
    public void applyCalculatedFines(FineCalculationRequest request) {
        for (Long voucherId : request.getVoucherIds()) {
            FeeVoucher voucher = feeVoucherRepository.findById(voucherId).orElse(null);
            if (voucher == null || voucher.getStatus() != FeeVoucher.VoucherStatus.PENDING) {
                continue;
            }

            BigDecimal calculatedFine = calculateFineForVoucher(voucherId, request.getCalculationDate());
            if (calculatedFine.compareTo(BigDecimal.ZERO) > 0) {
                voucher.setFineAmount(calculatedFine);
                if (voucher.getDueDate().isBefore(request.getCalculationDate())) {
                    voucher.markAsOverdue();
                }
                feeVoucherRepository.save(voucher);
            }
        }

        log.info("Applied calculated fines to {} vouchers", request.getVoucherIds().size());
    }

    @Transactional
    @Auditable(action = "WAIVE_FINE", entityType = "FeeVoucher")
    public void waiveFine(Long voucherId, String reason) {
        FeeVoucher voucher = feeVoucherRepository.findById(voucherId)
            .orElseThrow(() -> new RuntimeException("Voucher not found"));

        BigDecimal originalFine = voucher.getFineAmount();
        voucher.setFineAmount(BigDecimal.ZERO);
        voucher.setNotes((voucher.getNotes() != null ? voucher.getNotes() + "; " : "") +
                        "Fine waived: " + reason + " (Original: " + originalFine + ")");

        feeVoucherRepository.save(voucher);
        log.info("Fine waived for voucher {} - Original amount: {}, Reason: {}",
                 voucher.getVoucherNumber(), originalFine, reason);
    }

    private BigDecimal calculateFineForVoucher(Long voucherId, LocalDate calculationDate) {
        FeeVoucher voucher = feeVoucherRepository.findById(voucherId).orElse(null);
        if (voucher == null || voucher.getStatus() != FeeVoucher.VoucherStatus.PENDING) {
            return BigDecimal.ZERO;
        }

        if (!voucher.getDueDate().isBefore(calculationDate)) {
            return BigDecimal.ZERO; // Not overdue yet
        }

        // Get current enrollment to find class
        Long classId = voucher.getStudent().getEnrollments().stream()
            .filter(enrollment -> enrollment.getStatus() ==
                com.saqib.school.student.entity.StudentEnrollment.EnrollmentStatus.ACTIVE)
            .findFirst()
            .map(enrollment -> enrollment.getSchoolClass().getId())
            .orElse(null);

        if (classId == null) {
            return BigDecimal.ZERO;
        }

        int daysOverdue = (int) ChronoUnit.DAYS.between(voucher.getDueDate(), calculationDate);

        // Get applicable fine structures
        List<FineStructure> applicableFines = fineStructureRepository
            .findApplicableFines(classId, daysOverdue);

        if (applicableFines.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // Apply the highest applicable fine
        FineStructure applicableFine = applicableFines.getFirst(); // Already ordered DESC
        return applicableFine.calculateFine(voucher.getTotalAmount());
    }
}
