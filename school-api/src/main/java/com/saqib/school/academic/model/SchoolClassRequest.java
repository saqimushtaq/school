package com.saqib.school.academic.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolClassRequest {

  @NotNull(message = "Session ID is required")
  private Long sessionId;

  @NotBlank(message = "Class name is required")
  @Size(max = 50, message = "Class name must not exceed 50 characters")
  private String className;

  @Size(max = 10, message = "Section must not exceed 10 characters")
  private String section;

  @Min(value = 1, message = "Capacity must be at least 1")
  private Integer capacity;
}
