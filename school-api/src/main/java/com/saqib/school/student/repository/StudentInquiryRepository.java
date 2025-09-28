package com.saqib.school.student.repository;

import com.saqib.school.student.entity.StudentInquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StudentInquiryRepository extends JpaRepository<StudentInquiry, Long> {

    @Query("SELECT si FROM StudentInquiry si WHERE si.status = :status")
    Page<StudentInquiry> findByStatus(@Param("status") StudentInquiry.InquiryStatus status, Pageable pageable);

    @Query("SELECT si FROM StudentInquiry si WHERE si.inquirySource = :source")
    Page<StudentInquiry> findByInquirySource(@Param("source") StudentInquiry.InquirySource source, Pageable pageable);

    @Query("SELECT si FROM StudentInquiry si WHERE si.inquiryDate BETWEEN :startDate AND :endDate")
    Page<StudentInquiry> findByInquiryDateBetween(@Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate,
                                                 Pageable pageable);

    @Query("SELECT si FROM StudentInquiry si WHERE si.followUpDate <= :date AND si.status IN ('NEW', 'CONTACTED')")
    List<StudentInquiry> findInquiriesDueForFollowUp(@Param("date") LocalDate date);

    @Query("SELECT si FROM StudentInquiry si WHERE " +
           "LOWER(si.studentName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(si.parentName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "si.parentPhone LIKE CONCAT('%', :searchTerm, '%')")
    Page<StudentInquiry> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT si FROM StudentInquiry si WHERE si.parentPhone = :phone OR si.parentEmail = :email")
    List<StudentInquiry> findByParentPhoneOrEmail(@Param("phone") String phone, @Param("email") String email);

    @Query("SELECT COUNT(si) FROM StudentInquiry si WHERE si.status = :status")
    long countByStatus(@Param("status") StudentInquiry.InquiryStatus status);

    @Query("SELECT COUNT(si) FROM StudentInquiry si WHERE si.inquirySource = :source")
    long countByInquirySource(@Param("source") StudentInquiry.InquirySource source);

    @Query("SELECT si FROM StudentInquiry si WHERE si.createdBy.id = :userId")
    Page<StudentInquiry> findByCreatedBy(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT si FROM StudentInquiry si WHERE si.interestedClass = :className")
    Page<StudentInquiry> findByInterestedClass(@Param("className") String className, Pageable pageable);
}
