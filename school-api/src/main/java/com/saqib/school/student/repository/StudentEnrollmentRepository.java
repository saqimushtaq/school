package com.saqib.school.student.repository;

import com.saqib.school.student.entity.StudentEnrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentEnrollmentRepository extends JpaRepository<StudentEnrollment, Long> {

    @Query("SELECT se FROM StudentEnrollment se WHERE se.student.id = :studentId ORDER BY se.enrollmentDate DESC")
    List<StudentEnrollment> findByStudentIdOrderByEnrollmentDateDesc(@Param("studentId") Long studentId);

    @Query("SELECT se FROM StudentEnrollment se WHERE se.student.id = :studentId AND se.status = 'ACTIVE'")
    Optional<StudentEnrollment> findActiveEnrollmentByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT se FROM StudentEnrollment se WHERE se.schoolClass.id = :classId AND se.status = 'ACTIVE'")
    Page<StudentEnrollment> findActiveEnrollmentsByClassId(@Param("classId") Long classId, Pageable pageable);

    @Query("SELECT se FROM StudentEnrollment se WHERE se.schoolClass.id = :classId AND se.status = :status")
    Page<StudentEnrollment> findByClassIdAndStatus(@Param("classId") Long classId,
                                                  @Param("status") StudentEnrollment.EnrollmentStatus status,
                                                  Pageable pageable);

    @Query("SELECT se FROM StudentEnrollment se WHERE se.enrollmentDate BETWEEN :startDate AND :endDate")
    Page<StudentEnrollment> findByEnrollmentDateBetween(@Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate,
                                                       Pageable pageable);

    @Query("SELECT COUNT(se) FROM StudentEnrollment se WHERE se.schoolClass.id = :classId AND se.status = 'ACTIVE'")
    long countActiveEnrollmentsByClassId(@Param("classId") Long classId);

    @Query("SELECT se FROM StudentEnrollment se WHERE se.schoolClass.session.id = :sessionId AND se.status = 'ACTIVE'")
    Page<StudentEnrollment> findActiveEnrollmentsBySessionId(@Param("sessionId") Long sessionId, Pageable pageable);

    boolean existsByStudentIdAndSchoolClassIdAndStatus(Long studentId, Long classId, StudentEnrollment.EnrollmentStatus status);
}
