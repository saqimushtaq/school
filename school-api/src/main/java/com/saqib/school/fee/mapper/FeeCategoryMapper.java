package com.saqib.school.fee.mapper;

import com.saqib.school.fee.entity.FeeCategory;
import com.saqib.school.fee.model.FeeCategoryRequest;
import com.saqib.school.fee.model.FeeCategoryResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FeeCategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "feeStructures", ignore = true)
    @Mapping(target = "studentDiscounts", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FeeCategory toEntity(FeeCategoryRequest request);

    FeeCategoryResponse toResponse(FeeCategory feeCategory);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "feeStructures", ignore = true)
    @Mapping(target = "studentDiscounts", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(FeeCategoryRequest request, @MappingTarget FeeCategory feeCategory);
}
