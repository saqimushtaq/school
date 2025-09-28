package com.saqib.school.fee.mapper;

import com.saqib.school.fee.entity.FeeVoucherDetail;
import com.saqib.school.fee.model.FeeVoucherRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FeeVoucherDetailMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "voucher", ignore = true)
    @Mapping(target = "feeCategory", ignore = true)
    @Mapping(target = "finalAmount", expression = "java(request.getOriginalAmount().subtract(request.getDiscountAmount()))")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FeeVoucherDetail toEntity(FeeVoucherRequest.VoucherDetailRequest request);
}
