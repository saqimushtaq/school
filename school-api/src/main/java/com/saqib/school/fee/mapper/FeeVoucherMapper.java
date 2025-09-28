package com.saqib.school.fee.mapper;

import com.saqib.school.fee.entity.FeeVoucher;
import com.saqib.school.fee.entity.FeeVoucherDetail;
import com.saqib.school.fee.entity.FeePayment;
import com.saqib.school.fee.model.FeeVoucherRequest;
import com.saqib.school.fee.model.FeeVoucherResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {FeeVoucherDetailMapper.class, FeePaymentMapper.class})
public interface FeeVoucherMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "voucherNumber", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "issueDate", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "paidAmount", ignore = true)
    @Mapping(target = "fineAmount", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "paymentDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "voucherDetails", ignore = true)
    @Mapping(target = "payments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FeeVoucher toEntity(FeeVoucherRequest request);

    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", expression = "java(feeVoucher.getStudent().getFullName())")
    @Mapping(target = "studentRegistrationNumber", source = "student.registrationNumber")
    @Mapping(target = "remainingAmount", expression = "java(feeVoucher.getRemainingAmount())")
    @Mapping(target = "createdByName", expression = "java(getCreatedByName(feeVoucher))")
    @Mapping(target = "voucherDetails", source = "voucherDetails")
    @Mapping(target = "payments", source = "payments")
    FeeVoucherResponse toResponse(FeeVoucher feeVoucher);

    @Mapping(target = "feeCategoryName", source = "feeCategory.categoryName")
    FeeVoucherResponse.VoucherDetailResponse toVoucherDetailResponse(FeeVoucherDetail voucherDetail);

    @Mapping(target = "paymentMethod", expression = "java(payment.getPaymentMethod().toString())")
    @Mapping(target = "receivedByName", expression = "java(getReceivedByName(payment))")
    FeeVoucherResponse.PaymentResponse toPaymentResponse(FeePayment payment);

    List<FeeVoucherResponse> toResponseList(List<FeeVoucher> vouchers);

    default String getCreatedByName(FeeVoucher feeVoucher) {
        if (feeVoucher.getCreatedBy() != null) {
            return feeVoucher.getCreatedBy().getFirstName() + " " +
                   feeVoucher.getCreatedBy().getLastName();
        }
        return null;
    }

    default String getReceivedByName(FeePayment payment) {
        if (payment.getReceivedBy() != null) {
            return payment.getReceivedBy().getFirstName() + " " +
                   payment.getReceivedBy().getLastName();
        }
        return null;
    }
}
