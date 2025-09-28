package com.saqib.school.academic.entity;

import com.saqib.school.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "grade_boundaries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GradeBoundary extends BaseEntity {

  @Column(nullable = false, length = 5)
  private String grade;

  @Column(name = "min_percentage", nullable = false, precision = 5, scale = 2)
  private BigDecimal minPercentage;

  @Column(name = "max_percentage", nullable = false, precision = 5, scale = 2)
  private BigDecimal maxPercentage;

  @Column(name = "is_passing")
  @Builder.Default
  private Boolean isPassing = true;
}
