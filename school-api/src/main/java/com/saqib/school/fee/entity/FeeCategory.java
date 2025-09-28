package com.saqib.school.fee.entity;

import com.saqib.school.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Table(name = "fee_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FeeCategory extends BaseEntity {

    @Column(name = "category_name", nullable = false, unique = true, length = 100)
    private String categoryName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "feeCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<FeeStructure> feeStructures;

    @OneToMany(mappedBy = "feeCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<StudentDiscount> studentDiscounts;

    // Helper methods
    public boolean canBeDeleted() {
        return feeStructures == null || feeStructures.isEmpty();
    }
}
