package com.saqib.school.fee.mapper;

import com.saqib.school.fee.entity.FeeStructure;
import com.saqib.school.fee.model.BulkFeeUpdateRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BulkOperationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schoolClass", ignore = true)
    @Mapping(target = "feeCategory", ignore = true)
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "isMonthly", constant = "true")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FeeStructure toFeeStructure(BulkFeeUpdateRequest.FeeUpdateItem updateItem);
}
