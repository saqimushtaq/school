package com.saqib.school.academic.model;

import com.saqib.school.academic.entity.AcademicSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicSessionResponse {
  private Long id;
  private String sessionName;
  private LocalDate startDate;
  private LocalDate endDate;
  private AcademicSession.SessionStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
