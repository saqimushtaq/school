package com.saqib.school.fee.model;

import com.saqib.school.fee.entity.FeePayment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeePaymentResponse {
    private Long id;
    private Long voucherId;
    private String voucherNumber;
    private String studentName;
    private String studentRegistrationNumber;
    private FeePayment.PaymentMethod paymentMethod;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String referenceNumber;
    private String bankName;
    private String notes;
    private String receivedByName;
    private LocalDateTime createdAt;
}
