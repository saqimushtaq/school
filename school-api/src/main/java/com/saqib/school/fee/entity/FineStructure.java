package com.saqib.school.fee.entity;

import com.saqib.school.academic.entity.SchoolClass;
import com.saqib.school.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "fine_structures", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"class_id", "days_after_due"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FineStructure extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private SchoolClass schoolClass;

    @Column(name = "days_after_due", nullable = false)
    private Integer daysAfterDue;

    @Enumerated(EnumType.STRING)
    @Column(name = "fine_type", nullable = false, length = 20)
    private FineType fineType;

    @Column(name = "fine_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal fineValue;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    public enum FineType {
        PERCENTAGE, FIXED_AMOUNT
    }

    // Helper methods
    public BigDecimal calculateFine(BigDecimal voucherAmount) {
        return switch (fineType) {
            case PERCENTAGE -> voucherAmount.multiply(fineValue).divide(BigDecimal.valueOf(100));
            case FIXED_AMOUNT -> fineValue;
        };
    }
}
