package com.saqib.school.fee.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FineCalculationRequest {

    @NotNull(message = "Calculation date is required")
    private LocalDate calculationDate;

    @NotEmpty(message = "At least one voucher ID is required")
    private List<Long> voucherIds;
}
