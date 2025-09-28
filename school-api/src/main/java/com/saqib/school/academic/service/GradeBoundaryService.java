package com.saqib.school.academic.service;

import com.saqib.school.academic.entity.GradeBoundary;
import com.saqib.school.academic.repository.GradeBoundaryRepository;
import com.saqib.school.common.audit.Auditable;
import com.saqib.school.common.exception.BadRequestException;
import com.saqib.school.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GradeBoundaryService {

  private final GradeBoundaryRepository gradeBoundaryRepository;

  @Transactional
  @Auditable(action = "CREATE_GRADE_BOUNDARY", entityType = "GradeBoundary")
  public GradeBoundary createGradeBoundary(String grade, BigDecimal minPercentage,
                                           BigDecimal maxPercentage, Boolean isPassing) {

    validateGradeBoundary(grade, minPercentage, maxPercentage);

    if (gradeBoundaryRepository.existsByGrade(grade)) {
      throw new BadRequestException("Grade boundary for grade '" + grade + "' already exists");
    }

    GradeBoundary gradeBoundary = GradeBoundary.builder()
      .grade(grade)
      .minPercentage(minPercentage)
      .maxPercentage(maxPercentage)
      .isPassing(isPassing != null ? isPassing : true)
      .build();

    GradeBoundary saved = gradeBoundaryRepository.save(gradeBoundary);
    log.info("Grade boundary created for grade: {}", grade);

    return saved;
  }

  @Transactional(readOnly = true)
  public List<GradeBoundary> getAllGradeBoundaries() {
    return gradeBoundaryRepository.findAllByOrderByMinPercentageDesc();
  }

  @Transactional(readOnly = true)
  public String calculateGrade(BigDecimal percentage) {
    return gradeBoundaryRepository.findGradeByPercentage(percentage)
      .map(GradeBoundary::getGrade)
      .orElse("F");
  }

  @Transactional(readOnly = true)
  public GradeBoundary getGradeBoundaryById(Long id) {
    return gradeBoundaryRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Grade boundary", "id", id));
  }

  @Transactional
  @Auditable(action = "UPDATE_GRADE_BOUNDARY", entityType = "GradeBoundary")
  public GradeBoundary updateGradeBoundary(Long id, String grade, BigDecimal minPercentage,
                                           BigDecimal maxPercentage, Boolean isPassing) {

    GradeBoundary existing = getGradeBoundaryById(id);
    validateGradeBoundary(grade, minPercentage, maxPercentage);

    // Check if grade is being changed and new grade already exists
    if (!existing.getGrade().equals(grade) && gradeBoundaryRepository.existsByGrade(grade)) {
      throw new BadRequestException("Grade boundary for grade '" + grade + "' already exists");
    }

    existing.setGrade(grade);
    existing.setMinPercentage(minPercentage);
    existing.setMaxPercentage(maxPercentage);
    existing.setIsPassing(isPassing != null ? isPassing : existing.getIsPassing());

    GradeBoundary updated = gradeBoundaryRepository.save(existing);
    log.info("Grade boundary updated for grade: {}", grade);

    return updated;
  }

  @Transactional
  @Auditable(action = "DELETE_GRADE_BOUNDARY", entityType = "GradeBoundary")
  public void deleteGradeBoundary(Long id) {
    GradeBoundary gradeBoundary = getGradeBoundaryById(id);

    gradeBoundaryRepository.delete(gradeBoundary);
    log.info("Grade boundary deleted for grade: {}", gradeBoundary.getGrade());
  }

  @Transactional
  @Auditable(action = "SETUP_DEFAULT_GRADES", entityType = "GradeBoundary")
  public List<GradeBoundary> setupDefaultGradeBoundaries() {
    // Clear existing boundaries first
    gradeBoundaryRepository.deleteAll();

    List<GradeBoundary> defaultGrades = List.of(
      GradeBoundary.builder()
        .grade("A+")
        .minPercentage(new BigDecimal("95"))
        .maxPercentage(new BigDecimal("100"))
        .isPassing(true)
        .build(),
      GradeBoundary.builder()
        .grade("A")
        .minPercentage(new BigDecimal("85"))
        .maxPercentage(new BigDecimal("94"))
        .isPassing(true)
        .build(),
      GradeBoundary.builder()
        .grade("B")
        .minPercentage(new BigDecimal("75"))
        .maxPercentage(new BigDecimal("84"))
        .isPassing(true)
        .build(),
      GradeBoundary.builder()
        .grade("C")
        .minPercentage(new BigDecimal("65"))
        .maxPercentage(new BigDecimal("74"))
        .isPassing(true)
        .build(),
      GradeBoundary.builder()
        .grade("D")
        .minPercentage(new BigDecimal("50"))
        .maxPercentage(new BigDecimal("64"))
        .isPassing(true)
        .build(),
      GradeBoundary.builder()
        .grade("F")
        .minPercentage(new BigDecimal("0"))
        .maxPercentage(new BigDecimal("49"))
        .isPassing(false)
        .build()
    );

    List<GradeBoundary> saved = gradeBoundaryRepository.saveAll(defaultGrades);
    log.info("Default grade boundaries setup completed");

    return saved;
  }

  private void validateGradeBoundary(String grade, BigDecimal minPercentage, BigDecimal maxPercentage) {
    if (minPercentage.compareTo(BigDecimal.ZERO) < 0 || minPercentage.compareTo(new BigDecimal("100")) > 0) {
      throw new BadRequestException("Minimum percentage must be between 0 and 100");
    }

    if (maxPercentage.compareTo(BigDecimal.ZERO) < 0 || maxPercentage.compareTo(new BigDecimal("100")) > 0) {
      throw new BadRequestException("Maximum percentage must be between 0 and 100");
    }

    if (minPercentage.compareTo(maxPercentage) > 0) {
      throw new BadRequestException("Minimum percentage cannot be greater than maximum percentage");
    }
  }
}
