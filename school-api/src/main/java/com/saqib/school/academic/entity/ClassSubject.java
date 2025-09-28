package com.saqib.school.academic.entity;

import com.saqib.school.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "class_subjects", uniqueConstraints = {
  @UniqueConstraint(columnNames = {"class_id", "subject_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ClassSubject extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "class_id", nullable = false)
  private SchoolClass schoolClass;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "subject_id", nullable = false)
  private Subject subject;

  @Column(name = "total_marks")
  @Builder.Default
  private Integer totalMarks = 100;

  @Column(name = "passing_marks")
  @Builder.Default
  private Integer passingMarks = 40;
}
