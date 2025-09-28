package com.saqib.school.student.entity;

import com.saqib.school.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "student_guardians")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class StudentGuardian extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(name = "guardian_type", nullable = false, length = 20)
    private GuardianType guardianType;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 20)
    private String cnic;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(length = 100)
    private String occupation;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "is_primary_contact")
    @Builder.Default
    private Boolean isPrimaryContact = false;

    public enum GuardianType {
        FATHER, MOTHER, GUARDIAN
    }
}
