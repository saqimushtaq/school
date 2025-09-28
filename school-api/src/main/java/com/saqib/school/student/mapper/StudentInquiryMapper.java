package com.saqib.school.student.mapper;

import com.saqib.school.student.entity.StudentInquiry;
import com.saqib.school.student.model.StudentInquiryRequest;
import com.saqib.school.student.model.StudentInquiryResponse;
import com.saqib.school.student.model.StudentInquiryUpdateRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StudentInquiryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    StudentInquiry toEntity(StudentInquiryRequest request);

    @Mapping(target = "createdByUsername", source = "createdBy.username")
    StudentInquiryResponse toResponse(StudentInquiry inquiry);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "inquiryDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(StudentInquiryUpdateRequest request, @MappingTarget StudentInquiry inquiry);
}
