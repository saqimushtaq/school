package com.saqib.school.fee.repository;

import com.saqib.school.fee.entity.StudentDiscount;
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
public interface StudentDiscountRepository extends JpaRepository<StudentDiscount, Long> {

    @Query("SELECT sd FROM StudentDiscount sd WHERE sd.student.id = :studentId AND sd.isActive = true")
    List<StudentDiscount> findByStudentIdAndActive(@Param("studentId") Long studentId);

    @Query("SELECT sd FROM StudentDiscount sd WHERE sd.student.id = :studentId AND sd.feeCategory.id = :categoryId " +
           "AND sd.isActive = true AND sd.validFrom <= :date AND (sd.validTo IS NULL OR sd.validTo >= :date)")
    Optional<StudentDiscount> findValidDiscountByStudentAndCategory(@Param("studentId") Long studentId,
                                                                   @Param("categoryId") Long categoryId,
                                                                   @Param("date") LocalDate date);

    @Query("SELECT sd FROM StudentDiscount sd WHERE sd.feeCategory.id = :categoryId AND sd.isActive = true")
    Page<StudentDiscount> findByFeeCategoryIdAndActive(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT sd FROM StudentDiscount sd WHERE sd.validTo IS NOT NULL AND sd.validTo < :date AND sd.isActive = true")
    List<StudentDiscount> findExpiredDiscounts(@Param("date") LocalDate date);

    @Query("SELECT COUNT(sd) FROM StudentDiscount sd WHERE sd.student.id = :studentId AND sd.isActive = true")
    long countActiveDiscountsByStudentId(@Param("studentId") Long studentId);
}
