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
public class SchoolClassResponse {
  private Long id;
  private Long sessionId;
  private String sessionName;
  private String className;
  private String section;
  private Integer capacity;
  private Boolean isActive;
  private String displayName;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
