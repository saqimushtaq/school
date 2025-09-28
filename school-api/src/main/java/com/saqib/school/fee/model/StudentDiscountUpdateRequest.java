package com.saqib.school.fee.model;

import com.saqib.school.fee.entity.StudentDiscount;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDiscountUpdateRequest {

    private StudentDiscount.DiscountType discountType;

    @DecimalMin(value = "0.01", message = "Discount value must be greater than 0")
    private BigDecimal discountValue;

    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;

    private LocalDate validFrom;

    private LocalDate validTo;
}
