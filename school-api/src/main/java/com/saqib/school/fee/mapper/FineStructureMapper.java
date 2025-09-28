package com.saqib.school.fee.mapper;

import com.saqib.school.fee.entity.FineStructure;
import com.saqib.school.fee.model.FineStructureRequest;
import com.saqib.school.fee.model.FineStructureResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FineStructureMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schoolClass", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FineStructure toEntity(FineStructureRequest request);

    @Mapping(target = "classId", source = "schoolClass.id")
    @Mapping(target = "className", source = "schoolClass.className")
    @Mapping(target = "classSection", source = "schoolClass.section")
    FineStructureResponse toResponse(FineStructure fineStructure);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schoolClass", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(FineStructureRequest request, @MappingTarget FineStructure fineStructure);
}
