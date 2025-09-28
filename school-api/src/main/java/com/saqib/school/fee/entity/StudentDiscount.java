package com.saqib.school.fee.entity;

import com.saqib.school.common.entity.BaseEntity;
import com.saqib.school.student.entity.Student;
import com.saqib.school.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "student_discounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class StudentDiscount extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_category_id", nullable = false)
    private FeeCategory feeCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    public enum DiscountType {
        PERCENTAGE, FIXED_AMOUNT
    }

    // Helper methods
    public boolean isValidOn(LocalDate date) {
        return isActive &&
               !validFrom.isAfter(date) &&
               (validTo == null || !validTo.isBefore(date));
    }

    public BigDecimal calculateDiscount(BigDecimal originalAmount) {
        if (!isActive) return BigDecimal.ZERO;

        return switch (discountType) {
            case PERCENTAGE -> originalAmount.multiply(discountValue).divide(BigDecimal.valueOf(100));
            case FIXED_AMOUNT -> discountValue.min(originalAmount);
        };
    }
}
