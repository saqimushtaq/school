package com.saqib.school.academic.repository;

import com.saqib.school.academic.entity.ClassSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassSubjectRepository extends JpaRepository<ClassSubject, Long> {

  @Query("SELECT cs FROM ClassSubject cs WHERE cs.schoolClass.id = :classId")
  List<ClassSubject> findByClassId(@Param("classId") Long classId);

  @Query("SELECT cs FROM ClassSubject cs WHERE cs.subject.id = :subjectId")
  List<ClassSubject> findBySubjectId(@Param("subjectId") Long subjectId);

  @Query("SELECT cs FROM ClassSubject cs WHERE cs.schoolClass.id = :classId AND cs.subject.id = :subjectId")
  Optional<ClassSubject> findByClassIdAndSubjectId(@Param("classId") Long classId, @Param("subjectId") Long subjectId);

  boolean existsBySchoolClassIdAndSubjectId(Long classId, Long subjectId);
}
