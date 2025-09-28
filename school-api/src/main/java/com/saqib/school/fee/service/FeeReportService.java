package com.saqib.school.fee.service;

import com.saqib.school.fee.repository.FeePaymentRepository;
import com.saqib.school.fee.repository.FeeVoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeeReportService {

    private final FeeVoucherRepository feeVoucherRepository;
    private final FeePaymentRepository feePaymentRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> generateMonthlyCollectionReport(YearMonth month) {
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        BigDecimal totalCollection = feePaymentRepository.sumAmountBetweenDates(startDate, endDate);
        BigDecimal totalIssued = feeVoucherRepository.sumPaidAmountBetweenDates(startDate, endDate);
        long pendingVouchers = feeVoucherRepository.countPendingVouchers();

        Map<String, Object> report = new HashMap<>();
        report.put("month", month.toString());
        report.put("totalCollection", totalCollection != null ? totalCollection : BigDecimal.ZERO);
        report.put("totalIssued", totalIssued != null ? totalIssued : BigDecimal.ZERO);
        report.put("pendingVouchers", pendingVouchers);
        report.put("collectionPercentage", calculateCollectionPercentage(totalCollection, totalIssued));

        return report;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> generateAnnualCollectionSummary(int year) {
        Map<String, Object> summary = new HashMap<>();

        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(year, month);
            Map<String, Object> monthlyReport = generateMonthlyCollectionReport(yearMonth);
            summary.put(yearMonth.toString(), monthlyReport);
        }

        return summary;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> generateCollectionSummary(LocalDate startDate, LocalDate endDate) {
        BigDecimal totalCollection = feePaymentRepository.sumAmountBetweenDates(startDate, endDate);
        long totalVouchers = feeVoucherRepository.findByIssueDateBetween(startDate, endDate,
            org.springframework.data.domain.Pageable.unpaged()).getTotalElements();
        long pendingVouchers = feeVoucherRepository.countPendingVouchers();

        Map<String, Object> summary = new HashMap<>();
        summary.put("startDate", startDate);
        summary.put("endDate", endDate);
        summary.put("totalCollection", totalCollection != null ? totalCollection : BigDecimal.ZERO);
        summary.put("totalVouchers", totalVouchers);
        summary.put("pendingVouchers", pendingVouchers);
        summary.put("paidVouchers", totalVouchers - pendingVouchers);

        return summary;
    }

    private BigDecimal calculateCollectionPercentage(BigDecimal collected, BigDecimal issued) {
        if (issued == null || issued.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }
        if (collected == null) {
            return BigDecimal.ZERO;
        }
        return collected.divide(issued, 2, java.math.RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
    }
}
