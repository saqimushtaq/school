package com.saqib.school.academic.repository;

import com.saqib.school.academic.entity.SchoolClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {

  @Query("SELECT c FROM SchoolClass c WHERE c.session.id = :sessionId")
  Page<SchoolClass> findBySessionId(@Param("sessionId") Long sessionId, Pageable pageable);

  @Query("SELECT c FROM SchoolClass c WHERE c.session.id = :sessionId AND c.isActive = true")
  List<SchoolClass> findActiveClassesBySessionId(@Param("sessionId") Long sessionId);

  @Query("SELECT c FROM SchoolClass c WHERE c.session.id = :sessionId AND c.className = :className AND c.section = :section")
  Optional<SchoolClass> findBySessionIdAndClassNameAndSection(
    @Param("sessionId") Long sessionId,
    @Param("className") String className,
    @Param("section") String section);

  boolean existsBySessionIdAndClassNameAndSection(Long sessionId, String className, String section);
}
