package com.saqib.school.fee.mapper;

import com.saqib.school.fee.entity.StudentDiscount;
import com.saqib.school.fee.model.StudentDiscountRequest;
import com.saqib.school.fee.model.StudentDiscountResponse;
import com.saqib.school.fee.model.StudentDiscountUpdateRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface StudentDiscountMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", source = "studentId")
    @Mapping(target = "feeCategory", source = "feeCategoryId")
    @Mapping(target = "isActive", defaultValue = "true")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    StudentDiscount toEntity(StudentDiscountRequest request);

    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", expression = "java(studentDiscount.getStudent().getFullName())")
    @Mapping(target = "studentRegistrationNumber", source = "student.registrationNumber")
    @Mapping(target = "feeCategoryId", source = "feeCategory.id")
    @Mapping(target = "feeCategoryName", source = "feeCategory.categoryName")
    @Mapping(target = "createdByName", expression = "java(getCreatedByName(studentDiscount))")
    StudentDiscountResponse toResponse(StudentDiscount studentDiscount);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "feeCategory", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(StudentDiscountUpdateRequest request, @MappingTarget StudentDiscount studentDiscount);

    default String getCreatedByName(StudentDiscount studentDiscount) {
        if (studentDiscount.getCreatedBy() != null) {
            return studentDiscount.getCreatedBy().getFirstName() + " " +
                   studentDiscount.getCreatedBy().getLastName();
        }
        return null;
    }
}
