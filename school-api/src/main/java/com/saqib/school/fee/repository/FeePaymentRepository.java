package com.saqib.school.fee.repository;

import com.saqib.school.fee.entity.FeePayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FeePaymentRepository extends JpaRepository<FeePayment, Long> {

    @Query("SELECT fp FROM FeePayment fp WHERE fp.voucher.id = :voucherId ORDER BY fp.paymentDate DESC")
    List<FeePayment> findByVoucherId(@Param("voucherId") Long voucherId);

    @Query("SELECT fp FROM FeePayment fp WHERE fp.paymentDate BETWEEN :startDate AND :endDate")
    Page<FeePayment> findByPaymentDateBetween(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate,
                                             Pageable pageable);

    @Query("SELECT fp FROM FeePayment fp WHERE fp.paymentMethod = :method")
    Page<FeePayment> findByPaymentMethod(@Param("method") FeePayment.PaymentMethod method, Pageable pageable);

    @Query("SELECT SUM(fp.amount) FROM FeePayment fp WHERE fp.paymentDate BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT fp FROM FeePayment fp WHERE fp.receivedBy.id = :userId")
    Page<FeePayment> findByReceivedBy(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT fp FROM FeePayment fp WHERE fp.referenceNumber = :referenceNumber")
    List<FeePayment> findByReferenceNumber(@Param("referenceNumber") String referenceNumber);
}
