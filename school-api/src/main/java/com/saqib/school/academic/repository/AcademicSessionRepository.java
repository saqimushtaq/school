package com.saqib.school.academic.repository;

import com.saqib.school.academic.entity.AcademicSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AcademicSessionRepository extends JpaRepository<AcademicSession, Long> {

  Optional<AcademicSession> findBySessionName(String sessionName);

  Page<AcademicSession> findAllByOrderByStatusDesc(Pageable pageable);

  boolean existsBySessionName(String sessionName);

  @Query("SELECT s FROM AcademicSession s WHERE s.status = :status")
  Page<AcademicSession> findByStatus(@Param("status") AcademicSession.SessionStatus status, Pageable pageable);

  @Query("SELECT s FROM AcademicSession s WHERE s.status = 'ACTIVE'")
  Optional<AcademicSession> findActiveSession();

  @Query("SELECT s FROM AcademicSession s WHERE s.status = 'UPCOMING'")
  Optional<AcademicSession> findUpcomingSession();

  @Query("SELECT COUNT(s) FROM AcademicSession s WHERE s.status = :status")
  long countByStatus(@Param("status") AcademicSession.SessionStatus status);

  Page<AcademicSession> findBySessionNameContainingIgnoreCase(String search, Pageable pageable);
}
