package com.saqib.school.student.repository;

import com.saqib.school.student.entity.StudentGuardian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentGuardianRepository extends JpaRepository<StudentGuardian, Long> {

    @Query("SELECT sg FROM StudentGuardian sg WHERE sg.student.id = :studentId")
    List<StudentGuardian> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT sg FROM StudentGuardian sg WHERE sg.student.id = :studentId AND sg.isPrimaryContact = true")
    Optional<StudentGuardian> findPrimaryContactByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT sg FROM StudentGuardian sg WHERE sg.student.id = :studentId AND sg.guardianType = :guardianType")
    Optional<StudentGuardian> findByStudentIdAndGuardianType(@Param("studentId") Long studentId,
                                                            @Param("guardianType") StudentGuardian.GuardianType guardianType);

    @Query("SELECT sg FROM StudentGuardian sg WHERE sg.phone = :phone OR sg.email = :email")
    List<StudentGuardian> findByPhoneOrEmail(@Param("phone") String phone, @Param("email") String email);

    @Query("SELECT sg FROM StudentGuardian sg WHERE sg.cnic = :cnic")
    List<StudentGuardian> findByCnic(@Param("cnic") String cnic);

    void deleteByStudentId(Long studentId);
}
