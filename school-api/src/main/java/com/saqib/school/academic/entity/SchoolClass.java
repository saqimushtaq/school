package com.saqib.school.academic.entity;

import com.saqib.school.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Table(name = "classes", uniqueConstraints = {
  @UniqueConstraint(columnNames = {"session_id", "class_name", "section"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SchoolClass extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "session_id", nullable = false)
  private AcademicSession session;

  @Column(name = "class_name", nullable = false, length = 50)
  private String className;

  @Column(length = 10)
  @Builder.Default
  private String section = "A";

  @Builder.Default
  private Integer capacity = 30;

  @Column(name = "is_active")
  @Builder.Default
  private Boolean isActive = true;

  @OneToMany(mappedBy = "schoolClass", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<ClassSubject> classSubjects;

  // Helper method for display
  public String getDisplayName() {
    return className + " - " + section;
  }
}
