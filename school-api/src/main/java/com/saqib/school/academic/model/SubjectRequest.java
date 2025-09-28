package com.saqib.school.academic.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectRequest {

  @NotBlank(message = "Subject name is required")
  @Size(max = 100, message = "Subject name must not exceed 100 characters")
  private String subjectName;

  @Size(max = 20, message = "Subject code must not exceed 20 characters")
  private String subjectCode;
}
