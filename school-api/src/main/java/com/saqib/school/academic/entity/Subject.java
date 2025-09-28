package com.saqib.school.academic.entity;

import com.saqib.school.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Table(name = "subjects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Subject extends BaseEntity {

  @Column(name = "subject_name", nullable = false, length = 100)
  private String subjectName;

  @Column(name = "subject_code", length = 20)
  private String subjectCode;

  @Column(name = "is_active")
  @Builder.Default
  private Boolean isActive = true;

  @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<ClassSubject> classSubjects;
}
