package com.saqib.school.user.repository;

import com.saqib.school.user.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

  @Query("SELECT al FROM AuditLog al WHERE al.user.id = :userId")
  Page<AuditLog> findByUserId(@Param("userId") Long userId, Pageable pageable);

  @Query("SELECT al FROM AuditLog al WHERE al.entityType = :entityType AND al.entityId = :entityId")
  Page<AuditLog> findByEntityTypeAndEntityId(@Param("entityType") String entityType,
                                             @Param("entityId") Long entityId,
                                             Pageable pageable);

  @Query("SELECT al FROM AuditLog al WHERE al.createdAt BETWEEN :startDate AND :endDate")
  Page<AuditLog> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate,
                                 Pageable pageable);
}
