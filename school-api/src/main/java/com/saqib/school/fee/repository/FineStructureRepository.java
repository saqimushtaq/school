package com.saqib.school.fee.repository;

import com.saqib.school.fee.entity.FineStructure;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FineStructureRepository extends JpaRepository<FineStructure, Long> {

    @Query("SELECT fs FROM FineStructure fs WHERE fs.schoolClass.id = :classId AND fs.isActive = true ORDER BY fs.daysAfterDue")
    List<FineStructure> findByClassIdAndActiveOrderByDays(@Param("classId") Long classId);

    @Query("SELECT fs FROM FineStructure fs WHERE fs.schoolClass.id = :classId AND fs.daysAfterDue = :days")
    Optional<FineStructure> findByClassIdAndDaysAfterDue(@Param("classId") Long classId,
                                                        @Param("days") Integer daysAfterDue);

    @Query("SELECT fs FROM FineStructure fs WHERE fs.schoolClass.session.id = :sessionId AND fs.isActive = true")
    Page<FineStructure> findBySessionIdAndActive(@Param("sessionId") Long sessionId, Pageable pageable);

    @Query("SELECT fs FROM FineStructure fs WHERE fs.isActive = :isActive")
    Page<FineStructure> findByIsActive(@Param("isActive") Boolean isActive, Pageable pageable);

    boolean existsBySchoolClassIdAndDaysAfterDue(Long classId, Integer daysAfterDue);

    @Query("SELECT fs FROM FineStructure fs WHERE fs.schoolClass.id = :classId AND fs.daysAfterDue <= :days " +
           "AND fs.isActive = true ORDER BY fs.daysAfterDue DESC")
    List<FineStructure> findApplicableFines(@Param("classId") Long classId, @Param("days") Integer days);

    @Query("SELECT fs FROM FineStructure fs JOIN FETCH fs.schoolClass " +
           "WHERE fs.isActive = true ORDER BY fs.schoolClass.className, fs.daysAfterDue")
    List<FineStructure> findAllActiveWithClass();
}
