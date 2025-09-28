package com.saqib.school.fee.model;

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
public class DefaulterReportRequest {

    private LocalDate asOfDate; // If null, use current date

    private List<Long> classIds; // If empty, include all classes

    private Integer minimumDaysOverdue; // Filter by minimum days overdue

    private Boolean includePaidVouchers; // Include vouchers that are now paid but were overdue
}
