package com.saqib.school.fee.repository;

import com.saqib.school.fee.entity.FeeVoucherDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface FeeVoucherDetailRepository extends JpaRepository<FeeVoucherDetail, Long> {

    @Query("SELECT fvd FROM FeeVoucherDetail fvd WHERE fvd.voucher.id = :voucherId")
    List<FeeVoucherDetail> findByVoucherId(@Param("voucherId") Long voucherId);

    @Query("SELECT fvd FROM FeeVoucherDetail fvd WHERE fvd.feeCategory.id = :categoryId")
    List<FeeVoucherDetail> findByFeeCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT SUM(fvd.finalAmount) FROM FeeVoucherDetail fvd WHERE fvd.voucher.id = :voucherId")
    BigDecimal sumFinalAmountByVoucherId(@Param("voucherId") Long voucherId);

    @Query("SELECT SUM(fvd.discountAmount) FROM FeeVoucherDetail fvd WHERE fvd.voucher.id = :voucherId")
    BigDecimal sumDiscountAmountByVoucherId(@Param("voucherId") Long voucherId);

    @Query("SELECT fvd FROM FeeVoucherDetail fvd JOIN FETCH fvd.feeCategory WHERE fvd.voucher.id = :voucherId")
    List<FeeVoucherDetail> findByVoucherIdWithCategory(@Param("voucherId") Long voucherId);

    void deleteByVoucherId(Long voucherId);
}
