package com.saqib.school.fee.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class MonthlyVoucherGenerationRequest {

    @NotNull(message = "Month year is required")
    @Pattern(regexp = "^(0[1-9]|1[0-2])-\\d{4}$", message = "Month year must be in MM-YYYY format")
    private String monthYear;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    private List<Long> classIds; // If empty, generate for all active classes

    private List<Long> studentIds; // If empty, generate for all active students in specified classes
}
