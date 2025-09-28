package com.saqib.school.fee.repository;

import com.saqib.school.fee.entity.FeeStructure;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {

    @Query("SELECT fs FROM FeeStructure fs WHERE fs.schoolClass.id = :classId AND fs.isActive = true")
    List<FeeStructure> findByClassIdAndActive(@Param("classId") Long classId);

    @Query("SELECT fs FROM FeeStructure fs WHERE fs.feeCategory.id = :categoryId AND fs.isActive = true")
    List<FeeStructure> findByFeeCategoryIdAndActive(@Param("categoryId") Long categoryId);

    @Query("SELECT fs FROM FeeStructure fs WHERE fs.schoolClass.id = :classId AND fs.feeCategory.id = :categoryId")
    Optional<FeeStructure> findByClassIdAndCategoryId(@Param("classId") Long classId,
                                                     @Param("categoryId") Long categoryId);

    @Query("SELECT fs FROM FeeStructure fs WHERE fs.schoolClass.session.id = :sessionId AND fs.isActive = true")
    Page<FeeStructure> findBySessionIdAndActive(@Param("sessionId") Long sessionId, Pageable pageable);

    boolean existsBySchoolClassIdAndFeeCategoryId(Long classId, Long categoryId);

    @Query("SELECT fs FROM FeeStructure fs JOIN FETCH fs.schoolClass JOIN FETCH fs.feeCategory " +
           "WHERE fs.isActive = true ORDER BY fs.schoolClass.className, fs.feeCategory.categoryName")
    List<FeeStructure> findAllActiveWithDetails();
}
