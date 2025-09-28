package com.saqib.school.fee.mapper;

import com.saqib.school.fee.entity.FeeVoucher;
import com.saqib.school.fee.model.DefaulterReportResponse;
import com.saqib.school.student.entity.Student;
import com.saqib.school.student.entity.StudentGuardian;
import org.mapstruct.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Mapper(componentModel = "spring")
public interface DefaulterReportMapper {

    @Mapping(target = "studentId", source = "id")
    @Mapping(target = "studentName", expression = "java(student.getFullName())")
    @Mapping(target = "registrationNumber", source = "registrationNumber")
    @Mapping(target = "className", expression = "java(getCurrentClassName(student))")
    @Mapping(target = "guardianName", expression = "java(getPrimaryGuardianName(student))")
    @Mapping(target = "guardianPhone", expression = "java(getPrimaryGuardianPhone(student))")
    @Mapping(target = "totalOverdueVouchers", ignore = true)
    @Mapping(target = "totalOutstandingAmount", ignore = true)
    @Mapping(target = "totalFineAmount", ignore = true)
    @Mapping(target = "oldestDueDate", ignore = true)
    @Mapping(target = "daysSinceOldestDue", ignore = true)
    @Mapping(target = "overdueVouchers", ignore = true)
    DefaulterReportResponse.DefaulterInfo toDefaulterInfo(Student student);

    @Mapping(target = "voucherId", source = "id")
    @Mapping(target = "voucherType", expression = "java(voucher.getVoucherType().toString())")
    @Mapping(target = "daysOverdue", expression = "java(calculateDaysOverdue(voucher.getDueDate()))")
    @Mapping(target = "remainingAmount", expression = "java(voucher.getRemainingAmount())")
    DefaulterReportResponse.OverdueVoucherInfo toOverdueVoucherInfo(FeeVoucher voucher);

    default String getCurrentClassName(Student student) {
        return student.getEnrollments().stream()
            .filter(enrollment -> enrollment.getStatus().equals(
                com.saqib.school.student.entity.StudentEnrollment.EnrollmentStatus.ACTIVE))
            .findFirst()
            .map(enrollment -> enrollment.getSchoolClass().getDisplayName())
            .orElse("N/A");
    }

    default String getPrimaryGuardianName(Student student) {
        return student.getGuardians().stream()
            .filter(StudentGuardian::getIsPrimaryContact)
            .findFirst()
            .map(StudentGuardian::getName)
            .orElse(student.getGuardians().stream()
                .findFirst()
                .map(StudentGuardian::getName)
                .orElse("N/A"));
    }

    default String getPrimaryGuardianPhone(Student student) {
        return student.getGuardians().stream()
            .filter(StudentGuardian::getIsPrimaryContact)
            .findFirst()
            .map(StudentGuardian::getPhone)
            .orElse(student.getGuardians().stream()
                .findFirst()
                .map(StudentGuardian::getPhone)
                .orElse("N/A"));
    }

    default Integer calculateDaysOverdue(LocalDate dueDate) {
        return (int) ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }
}
