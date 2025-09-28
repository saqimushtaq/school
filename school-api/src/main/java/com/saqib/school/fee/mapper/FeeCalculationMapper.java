package com.saqib.school.fee.mapper;

import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface FeeCalculationMapper {

    default Map<String, Object> createCalculationResult(
            Long voucherId,
            BigDecimal originalAmount,
            BigDecimal fineAmount,
            BigDecimal newTotalAmount,
            String status) {

        return Map.of(
            "voucherId", voucherId,
            "originalAmount", originalAmount,
            "fineAmount", fineAmount,
            "newTotalAmount", newTotalAmount,
            "status", status
        );
    }

    default Map<String, Object> createDiscountCalculationResult(
            Long studentId,
            Long categoryId,
            BigDecimal originalAmount,
            BigDecimal discountAmount,
            BigDecimal finalAmount,
            String discountType) {

        return Map.of(
            "studentId", studentId,
            "categoryId", categoryId,
            "originalAmount", originalAmount,
            "discountAmount", discountAmount,
            "finalAmount", finalAmount,
            "discountType", discountType
        );
    }
}
