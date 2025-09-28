package com.saqib.school.academic.repository;

import com.saqib.school.academic.entity.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

  Optional<Subject> findBySubjectName(String subjectName);

  Optional<Subject> findBySubjectCode(String subjectCode);

  boolean existsBySubjectName(String subjectName);

  boolean existsBySubjectCode(String subjectCode);

  @Query("SELECT s FROM Subject s WHERE s.isActive = true")
  List<Subject> findActiveSubjects();

  @Query("SELECT s FROM Subject s WHERE s.isActive = :isActive")
  Page<Subject> findByIsActive(@Param("isActive") Boolean isActive, Pageable pageable);
}
