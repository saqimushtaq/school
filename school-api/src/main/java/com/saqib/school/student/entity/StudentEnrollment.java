package com.saqib.school.student.entity;

import com.saqib.school.academic.entity.SchoolClass;
import com.saqib.school.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "student_enrollments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class StudentEnrollment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private SchoolClass schoolClass;

    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate;

    @Column(name = "completion_date")
    private LocalDate completionDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;

    public enum EnrollmentStatus {
        ACTIVE, COMPLETED, TRANSFERRED
    }

    // Helper methods
    public boolean isActive() {
        return EnrollmentStatus.ACTIVE.equals(status);
    }

    public void complete() {
        this.status = EnrollmentStatus.COMPLETED;
        this.completionDate = LocalDate.now();
    }

    public void transfer() {
        this.status = EnrollmentStatus.TRANSFERRED;
        this.completionDate = LocalDate.now();
    }
}
