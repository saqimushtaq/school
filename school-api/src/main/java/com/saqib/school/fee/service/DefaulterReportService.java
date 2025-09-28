package com.saqib.school.fee.service;

import com.saqib.school.fee.entity.FeeVoucher;
import com.saqib.school.fee.mapper.DefaulterReportMapper;
import com.saqib.school.fee.model.DefaulterReportRequest;
import com.saqib.school.fee.model.DefaulterReportResponse;
import com.saqib.school.fee.repository.FeeVoucherRepository;
import com.saqib.school.student.entity.Student;
import com.saqib.school.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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
public class DefaulterReportService {

    private final StudentRepository studentRepository;
    private final FeeVoucherRepository feeVoucherRepository;
    private final DefaulterReportMapper defaulterReportMapper;

    @Transactional(readOnly = true)
    public DefaulterReportResponse generateDefaulterReport(DefaulterReportRequest request) {
        LocalDate asOfDate = request.getAsOfDate() != null ? request.getAsOfDate() : LocalDate.now();

        // Get overdue vouchers
        List<FeeVoucher> overdueVouchers = feeVoucherRepository.findOverdueVouchers(asOfDate);

        // Filter by minimum days overdue if specified
        if (request.getMinimumDaysOverdue() != null) {
            overdueVouchers = overdueVouchers.stream()
                .filter(voucher -> ChronoUnit.DAYS.between(voucher.getDueDate(), asOfDate) >= request.getMinimumDaysOverdue())
                .toList();
        }

        // Group by student
        Map<Student, List<FeeVoucher>> defaulterMap = overdueVouchers.stream()
            .collect(Collectors.groupingBy(FeeVoucher::getStudent));

        // Filter by class if specified
        if (request.getClassIds() != null && !request.getClassIds().isEmpty()) {
            defaulterMap = defaulterMap.entrySet().stream()
                .filter(entry -> {
                    Student student = entry.getKey();
                    return student.getEnrollments().stream()
                        .filter(enrollment -> enrollment.getStatus() ==
                            com.saqib.school.student.entity.StudentEnrollment.EnrollmentStatus.ACTIVE)
                        .anyMatch(enrollment -> request.getClassIds().contains(enrollment.getSchoolClass().getId()));
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        // Create defaulter info list
      List<DefaulterReportResponse.DefaulterInfo> defaulters = defaulterMap.entrySet().stream()
        .map(entry -> createDefaulterInfo(entry.getKey(), entry.getValue(), asOfDate))
        .sorted((d1, d2) -> d2.getDaysSinceOldestDue().compareTo(d1.getDaysSinceOldestDue()))
        .toList();

      // Calculate totals
      BigDecimal totalOutstanding = defaulters.stream()
        .map(DefaulterReportResponse.DefaulterInfo::getTotalOutstandingAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

      return DefaulterReportResponse.builder()
        .reportDate(asOfDate)
        .totalDefaulters(defaulters.size())
        .totalOutstandingAmount(totalOutstanding)
        .defaulters(defaulters)
        .build();
    }

  private DefaulterReportResponse.DefaulterInfo createDefaulterInfo(Student student, List<FeeVoucher> overdueVouchers, LocalDate asOfDate) {
    DefaulterReportResponse.DefaulterInfo defaulterInfo = defaulterReportMapper.toDefaulterInfo(student);

    // Calculate overdue voucher details
    List<DefaulterReportResponse.OverdueVoucherInfo> overdueVoucherInfos = overdueVouchers.stream()
      .map(defaulterReportMapper::toOverdueVoucherInfo)
      .toList();

    // Calculate aggregated values
    BigDecimal totalOutstanding = overdueVouchers.stream()
      .map(FeeVoucher::getRemainingAmount)
      .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal totalFines = overdueVouchers.stream()
      .map(FeeVoucher::getFineAmount)
      .reduce(BigDecimal.ZERO, BigDecimal::add);

    LocalDate oldestDueDate = overdueVouchers.stream()
      .map(FeeVoucher::getDueDate)
      .min(LocalDate::compareTo)
      .orElse(asOfDate);

    int daysSinceOldest = (int) ChronoUnit.DAYS.between(oldestDueDate, asOfDate);

    // Set calculated fields
    defaulterInfo.setTotalOverdueVouchers(overdueVouchers.size());
    defaulterInfo.setTotalOutstandingAmount(totalOutstanding);
    defaulterInfo.setTotalFineAmount(totalFines);
    defaulterInfo.setOldestDueDate(oldestDueDate);
    defaulterInfo.setDaysSinceOldestDue(daysSinceOldest);
    defaulterInfo.setOverdueVouchers(overdueVoucherInfos);

    return defaulterInfo;
  }
}
