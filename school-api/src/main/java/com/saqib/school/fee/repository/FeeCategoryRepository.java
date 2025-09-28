package com.saqib.school.fee.repository;

import com.saqib.school.fee.entity.FeeCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeeCategoryRepository extends JpaRepository<FeeCategory, Long> {

    Optional<FeeCategory> findByCategoryName(String categoryName);

    boolean existsByCategoryName(String categoryName);

    @Query("SELECT fc FROM FeeCategory fc WHERE fc.isActive = :isActive")
    Page<FeeCategory> findByIsActive(@Param("isActive") Boolean isActive, Pageable pageable);

    @Query("SELECT fc FROM FeeCategory fc WHERE fc.isActive = true ORDER BY fc.categoryName")
    List<FeeCategory> findAllActive();

    @Query("SELECT fc FROM FeeCategory fc WHERE " +
           "LOWER(fc.categoryName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(fc.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<FeeCategory> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);
}
