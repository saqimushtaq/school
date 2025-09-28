package com.saqib.school.fee.service;

import com.saqib.school.common.audit.Auditable;
import com.saqib.school.common.dto.PageResponse;
import com.saqib.school.common.exception.BadRequestException;
import com.saqib.school.common.exception.ResourceNotFoundException;
import com.saqib.school.fee.entity.FeePayment;
import com.saqib.school.fee.entity.FeeVoucher;
import com.saqib.school.fee.mapper.FeePaymentMapper;
import com.saqib.school.fee.model.FeePaymentRequest;
import com.saqib.school.fee.model.FeePaymentResponse;
import com.saqib.school.fee.repository.FeePaymentRepository;
import com.saqib.school.fee.repository.FeeVoucherRepository;
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
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeePaymentService {

    private final FeePaymentRepository feePaymentRepository;
    private final FeeVoucherRepository feeVoucherRepository;
    private final UserRepository userRepository;
    private final FeePaymentMapper feePaymentMapper;

    @Transactional
    @Auditable(action = "PROCESS_FEE_PAYMENT", entityType = "FeePayment")
    public FeePaymentResponse processFeePayment(FeePaymentRequest request) {
        FeeVoucher voucher = findFeeVoucherById(request.getVoucherId());
        User currentUser = getCurrentUser();

        validatePaymentRequest(request, voucher);

        FeePayment payment = feePaymentMapper.toEntity(request);
        payment.setVoucher(voucher);
        payment.setReceivedBy(currentUser);

        FeePayment savedPayment = feePaymentRepository.save(payment);

        // Update voucher payment status
        updateVoucherPaymentStatus(voucher, request.getAmount());

        log.info("Payment processed: {} for voucher {} - Amount: {}",
                 savedPayment.getId(), voucher.getVoucherNumber(), request.getAmount());

        return feePaymentMapper.toResponse(savedPayment);
    }

    @Transactional(readOnly = true)
    public FeePaymentResponse getFeePaymentById(Long id) {
        FeePayment payment = findFeePaymentById(id);
        return feePaymentMapper.toResponse(payment);
    }

    @Transactional(readOnly = true)
    public List<FeePaymentResponse> getVoucherPayments(Long voucherId) {
        return feePaymentRepository.findByVoucherId(voucherId)
            .stream()
            .map(feePaymentMapper::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<FeePaymentResponse> getPaymentsByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        var page = feePaymentRepository.findByPaymentDateBetween(startDate, endDate, pageable)
            .map(feePaymentMapper::toResponse);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public PageResponse<FeePaymentResponse> getPaymentsByMethod(FeePayment.PaymentMethod method, Pageable pageable) {
        var page = feePaymentRepository.findByPaymentMethod(method, pageable)
            .map(feePaymentMapper::toResponse);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public PageResponse<FeePaymentResponse> getPaymentsByUser(Long userId, Pageable pageable) {
        var page = feePaymentRepository.findByReceivedBy(userId, pageable)
            .map(feePaymentMapper::toResponse);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalCollectionForPeriod(LocalDate startDate, LocalDate endDate) {
        BigDecimal total = feePaymentRepository.sumAmountBetweenDates(startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public List<FeePaymentResponse> getPaymentsByReference(String referenceNumber) {
        return feePaymentRepository.findByReferenceNumber(referenceNumber)
            .stream()
            .map(feePaymentMapper::toResponse)
            .toList();
    }

    private void validatePaymentRequest(FeePaymentRequest request, FeeVoucher voucher) {
        if (voucher.getStatus() == FeeVoucher.VoucherStatus.CANCELLED) {
            throw new BadRequestException("Cannot process payment for cancelled voucher");
        }

        if (voucher.getStatus() == FeeVoucher.VoucherStatus.PAID) {
            throw new BadRequestException("Voucher is already fully paid");
        }

        BigDecimal remainingAmount = voucher.getRemainingAmount();
        if (request.getAmount().compareTo(remainingAmount) > 0) {
            throw new BadRequestException("Payment amount cannot exceed remaining amount: " + remainingAmount);
        }

        if (request.getPaymentDate().isAfter(LocalDate.now())) {
            throw new BadRequestException("Payment date cannot be in the future");
        }
    }

    private void updateVoucherPaymentStatus(FeeVoucher voucher, BigDecimal paymentAmount) {
        BigDecimal newPaidAmount = voucher.getPaidAmount().add(paymentAmount);
        voucher.setPaidAmount(newPaidAmount);

        BigDecimal totalAmountWithFine = voucher.getTotalAmount().add(voucher.getFineAmount());
        if (newPaidAmount.compareTo(totalAmountWithFine) >= 0) {
            voucher.markAsPaid();
        }

        feeVoucherRepository.save(voucher);
    }

    private FeePayment findFeePaymentById(Long id) {
        return feePaymentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Fee Payment", "id", id));
    }

    private FeeVoucher findFeeVoucherById(Long id) {
        return feeVoucherRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Fee Voucher", "id", id));
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }
}
