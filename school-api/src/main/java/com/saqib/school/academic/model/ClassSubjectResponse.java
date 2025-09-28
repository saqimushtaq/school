package com.saqib.school.academic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassSubjectResponse {
  private Long id;
  private Long classId;
  private String className;
  private String section;
  private Long subjectId;
  private String subjectName;
  private String subjectCode;
  private Integer totalMarks;
  private Integer passingMarks;
  private LocalDateTime createdAt;
}
