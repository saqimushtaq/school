package com.saqib.school.fee.entity;

import com.saqib.school.common.entity.BaseEntity;
import com.saqib.school.student.entity.Student;
import com.saqib.school.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "fee_vouchers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FeeVoucher extends BaseEntity {

    @Column(name = "voucher_number", nullable = false, unique = true, length = 50)
    private String voucherNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(name = "voucher_type", nullable = false, length = 20)
    private VoucherType voucherType;

    @Column(name = "month_year", length = 7)
    private String monthYear; // MM-YYYY for monthly fees

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "paid_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(name = "fine_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal fineAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private VoucherStatus status = VoucherStatus.PENDING;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<FeeVoucherDetail> voucherDetails;

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<FeePayment> payments;

    public enum VoucherType {
        ADMISSION, MONTHLY, INSTALLMENT
    }

    public enum VoucherStatus {
        PENDING, PAID, OVERDUE, CANCELLED
    }

    // Helper methods
    public BigDecimal getRemainingAmount() {
        return totalAmount.add(fineAmount).subtract(paidAmount);
    }

    public boolean isPaid() {
        return VoucherStatus.PAID.equals(status);
    }

    public boolean isOverdue() {
        return VoucherStatus.OVERDUE.equals(status) ||
               (VoucherStatus.PENDING.equals(status) && dueDate.isBefore(LocalDate.now()));
    }

    public void markAsPaid() {
        this.status = VoucherStatus.PAID;
        this.paymentDate = LocalDate.now();
    }

    public void markAsOverdue() {
        if (VoucherStatus.PENDING.equals(this.status)) {
            this.status = VoucherStatus.OVERDUE;
        }
    }

    public void cancel() {
        this.status = VoucherStatus.CANCELLED;
    }
}
