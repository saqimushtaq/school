package com.saqib.school.student.entity;

import com.saqib.school.common.entity.BaseEntity;
import com.saqib.school.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "student_inquiries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class StudentInquiry extends BaseEntity {

    @Column(name = "inquiry_date", nullable = false)
    private LocalDate inquiryDate;

    @Column(name = "student_name", nullable = false, length = 100)
    private String studentName;

    @Column(name = "parent_name", nullable = false, length = 100)
    private String parentName;

    @Column(name = "parent_phone", nullable = false, length = 20)
    private String parentPhone;

    @Column(name = "parent_email", length = 100)
    private String parentEmail;

    @Column(name = "interested_class", length = 50)
    private String interestedClass;

    @Enumerated(EnumType.STRING)
    @Column(name = "inquiry_source", length = 50)
    private InquirySource inquirySource;

    @Column(name = "referral_details", columnDefinition = "TEXT")
    private String referralDetails;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private InquiryStatus status = InquiryStatus.NEW;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    @Column(name = "registration_fee_paid")
    @Builder.Default
    private Boolean registrationFeePaid = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    public enum InquirySource {
        REFERRAL, WALK_IN, ADVERTISEMENT, ONLINE, SOCIAL_MEDIA, OTHER
    }

    public enum InquiryStatus {
        NEW, CONTACTED, INTERESTED, ADMITTED, REJECTED, LOST
    }

    // Helper methods
    public boolean canBeContacted() {
        return InquiryStatus.NEW.equals(status);
    }

    public boolean canBeAdmitted() {
        return InquiryStatus.INTERESTED.equals(status);
    }

    public void markAsContacted() {
        this.status = InquiryStatus.CONTACTED;
    }

    public void markAsInterested() {
        this.status = InquiryStatus.INTERESTED;
    }

    public void markAsAdmitted() {
        this.status = InquiryStatus.ADMITTED;
    }

    public void markAsRejected() {
        this.status = InquiryStatus.REJECTED;
    }
}
