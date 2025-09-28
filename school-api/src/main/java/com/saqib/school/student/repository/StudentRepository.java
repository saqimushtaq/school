package com.saqib.school.student.repository;

import com.saqib.school.student.entity.Student;
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
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByRegistrationNumber(String registrationNumber);

    boolean existsByRegistrationNumber(String registrationNumber);

    @Query("SELECT s FROM Student s WHERE s.status = :status")
    Page<Student> findByStatus(@Param("status") Student.StudentStatus status, Pageable pageable);

    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "s.registrationNumber LIKE CONCAT('%', :searchTerm, '%')")
    Page<Student> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT s FROM Student s JOIN s.enrollments e WHERE e.schoolClass.id = :classId AND e.status = 'ACTIVE'")
    Page<Student> findByActiveEnrollmentInClass(@Param("classId") Long classId, Pageable pageable);

    @Query("SELECT s FROM Student s WHERE s.admissionDate BETWEEN :startDate AND :endDate")
    Page<Student> findByAdmissionDateBetween(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate,
                                           Pageable pageable);

    @Query("SELECT COUNT(s) FROM Student s WHERE s.status = 'ACTIVE'")
    long countActiveStudents();

    @Query("SELECT COUNT(s) FROM Student s JOIN s.enrollments e WHERE e.schoolClass.id = :classId AND e.status = 'ACTIVE'")
    long countStudentsInClass(@Param("classId") Long classId);

    @Query("SELECT s FROM Student s WHERE s.dateOfBirth = :dateOfBirth")
    List<Student> findByDateOfBirth(@Param("dateOfBirth") LocalDate dateOfBirth);

    @Query("SELECT s FROM Student s WHERE s.phone = :phone OR s.email = :email")
    List<Student> findByPhoneOrEmail(@Param("phone") String phone, @Param("email") String email);
}
