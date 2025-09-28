package com.saqib.school.fee.mapper;

import com.saqib.school.fee.entity.FeeStructure;
import com.saqib.school.fee.model.FeeStructureRequest;
import com.saqib.school.fee.model.FeeStructureResponse;
import com.saqib.school.fee.model.FeeStructureUpdateRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FeeStructureMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schoolClass", ignore = true)
    @Mapping(target = "feeCategory", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FeeStructure toEntity(FeeStructureRequest request);

    @Mapping(target = "classId", source = "schoolClass.id")
    @Mapping(target = "className", source = "schoolClass.className")
    @Mapping(target = "classSection", source = "schoolClass.section")
    @Mapping(target = "feeCategoryId", source = "feeCategory.id")
    @Mapping(target = "feeCategoryName", source = "feeCategory.categoryName")
    FeeStructureResponse toResponse(FeeStructure feeStructure);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schoolClass", ignore = true)
    @Mapping(target = "feeCategory", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(FeeStructureUpdateRequest request, @MappingTarget FeeStructure feeStructure);
}
