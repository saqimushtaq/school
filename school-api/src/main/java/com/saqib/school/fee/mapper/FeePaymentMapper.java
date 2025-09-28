package com.saqib.school.fee.mapper;

import com.saqib.school.fee.entity.FeePayment;
import com.saqib.school.fee.model.FeePaymentRequest;
import com.saqib.school.fee.model.FeePaymentResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FeePaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "voucher", ignore = true)
    @Mapping(target = "receivedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FeePayment toEntity(FeePaymentRequest request);

    @Mapping(target = "voucherId", source = "voucher.id")
    @Mapping(target = "voucherNumber", source = "voucher.voucherNumber")
    @Mapping(target = "studentName", expression = "java(payment.getVoucher().getStudent().getFullName())")
    @Mapping(target = "studentRegistrationNumber", source = "voucher.student.registrationNumber")
    @Mapping(target = "receivedByName", expression = "java(getReceivedByName(payment))")
    FeePaymentResponse toResponse(FeePayment payment);

    default String getReceivedByName(FeePayment payment) {
        if (payment.getReceivedBy() != null) {
            return payment.getReceivedBy().getFirstName() + " " +
                   payment.getReceivedBy().getLastName();
        }
        return null;
    }
}
