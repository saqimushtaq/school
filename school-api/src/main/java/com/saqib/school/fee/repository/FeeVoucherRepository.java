package com.saqib.school.fee.repository;

import com.saqib.school.fee.entity.FeeVoucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeeVoucherRepository extends JpaRepository<FeeVoucher, Long> {

    Optional<FeeVoucher> findByVoucherNumber(String voucherNumber);

    boolean existsByVoucherNumber(String voucherNumber);

    @Query("SELECT fv FROM FeeVoucher fv WHERE fv.student.id = :studentId ORDER BY fv.issueDate DESC")
    Page<FeeVoucher> findByStudentId(@Param("studentId") Long studentId, Pageable pageable);

    @Query("SELECT fv FROM FeeVoucher fv WHERE fv.status = :status")
    Page<FeeVoucher> findByStatus(@Param("status") FeeVoucher.VoucherStatus status, Pageable pageable);

    @Query("SELECT fv FROM FeeVoucher fv WHERE fv.voucherType = :type")
    Page<FeeVoucher> findByVoucherType(@Param("type") FeeVoucher.VoucherType type, Pageable pageable);

    @Query("SELECT fv FROM FeeVoucher fv WHERE fv.dueDate < :date AND fv.status = 'PENDING'")
    List<FeeVoucher> findOverdueVouchers(@Param("date") LocalDate date);

    @Query("SELECT fv FROM FeeVoucher fv WHERE fv.monthYear = :monthYear")
    Page<FeeVoucher> findByMonthYear(@Param("monthYear") String monthYear, Pageable pageable);

    @Query("SELECT fv FROM FeeVoucher fv WHERE fv.issueDate BETWEEN :startDate AND :endDate")
    Page<FeeVoucher> findByIssueDateBetween(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate,
                                           Pageable pageable);

    @Query("SELECT COUNT(fv) FROM FeeVoucher fv WHERE fv.status = 'PENDING'")
    long countPendingVouchers();

    @Query("SELECT SUM(fv.totalAmount) FROM FeeVoucher fv WHERE fv.status = 'PAID' AND fv.paymentDate BETWEEN :startDate AND :endDate")
    java.math.BigDecimal sumPaidAmountBetweenDates(@Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);
}
