package com.saqib.school.academic.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassSubjectRequest {

  @NotNull(message = "Class ID is required")
  private Long classId;

  @NotNull(message = "Subject ID is required")
  private Long subjectId;

  @Min(value = 1, message = "Total marks must be at least 1")
  @Max(value = 1000, message = "Total marks cannot exceed 1000")
  private Integer totalMarks;

  @Min(value = 0, message = "Passing marks cannot be negative")
  private Integer passingMarks;
}
