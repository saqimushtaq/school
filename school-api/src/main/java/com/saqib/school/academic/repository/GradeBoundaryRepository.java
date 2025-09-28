package com.saqib.school.academic.repository;

import com.saqib.school.academic.entity.GradeBoundary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface GradeBoundaryRepository extends JpaRepository<GradeBoundary, Long> {

  List<GradeBoundary> findAllByOrderByMinPercentageDesc();

  @Query("SELECT gb FROM GradeBoundary gb WHERE :percentage >= gb.minPercentage AND :percentage <= gb.maxPercentage")
  Optional<GradeBoundary> findGradeByPercentage(@Param("percentage") BigDecimal percentage);

  boolean existsByGrade(String grade);
}
