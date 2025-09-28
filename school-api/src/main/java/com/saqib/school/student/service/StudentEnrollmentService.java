package com.saqib.school.student.service;

import com.saqib.school.academic.entity.SchoolClass;
import com.saqib.school.academic.repository.SchoolClassRepository;
import com.saqib.school.common.audit.Auditable;
import com.saqib.school.common.dto.PageResponse;
import com.saqib.school.common.exception.BadRequestException;
import com.saqib.school.common.exception.ResourceNotFoundException;
import com.saqib.school.student.entity.Student;
import com.saqib.school.student.entity.StudentEnrollment;
import com.saqib.school.student.mapper.StudentEnrollmentMapper;
import com.saqib.school.student.model.StudentEnrollmentRequest;
import com.saqib.school.student.model.StudentEnrollmentResponse;
import com.saqib.school.student.repository.StudentEnrollmentRepository;
import com.saqib.school.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentEnrollmentService {

  private final StudentEnrollmentRepository enrollmentRepository;
  private final StudentRepository studentRepository;
  private final SchoolClassRepository classRepository;
  private final StudentEnrollmentMapper enrollmentMapper;

  @Transactional
  @Auditable(action = "ENROLL_STUDENT", entityType = "StudentEnrollment")
  public StudentEnrollmentResponse enrollStudent(StudentEnrollmentRequest request) {
    return enrollStudent(request.getStudentId(), request.getClassId(), request.getEnrollmentDate());
  }

  @Transactional
  @Auditable(action = "ENROLL_STUDENT", entityType = "StudentEnrollment")
  public StudentEnrollmentResponse enrollStudent(Long studentId, Long classId, LocalDate enrollmentDate) {
    Student student = findStudentById(studentId);
    SchoolClass schoolClass = findClassById(classId);

    validateEnrollment(student, schoolClass, enrollmentDate);

    // Complete any existing active enrollment
    enrollmentRepository.findActiveEnrollmentByStudentId(studentId)
      .ifPresent(existingEnrollment -> {
        existingEnrollment.complete();
        enrollmentRepository.save(existingEnrollment);
      });

    StudentEnrollment enrollment = StudentEnrollment.builder()
      .student(student)
      .schoolClass(schoolClass)
      .enrollmentDate(enrollmentDate)
      .status(StudentEnrollment.EnrollmentStatus.ACTIVE)
      .build();

    StudentEnrollment savedEnrollment = enrollmentRepository.save(enrollment);

    log.info("Student {} enrolled in class {} on {}",
      student.getFullName(), schoolClass.getDisplayName(), enrollmentDate);

    return enrollmentMapper.toResponse(savedEnrollment);
  }

  @Transactional(readOnly = true)
  public StudentEnrollmentResponse getEnrollmentById(Long enrollmentId) {
    StudentEnrollment enrollment = findEnrollmentById(enrollmentId);
    return enrollmentMapper.toResponse(enrollment);
  }

  @Transactional(readOnly = true)
  public StudentEnrollmentResponse getActiveEnrollment(Long studentId) {
    StudentEnrollment enrollment = enrollmentRepository.findActiveEnrollmentByStudentId(studentId)
      .orElseThrow(() -> new ResourceNotFoundException("Active enrollment not found for student"));
    return enrollmentMapper.toResponse(enrollment);
  }

  @Transactional(readOnly = true)
  public List<StudentEnrollmentResponse> getStudentEnrollmentHistory(Long studentId) {
    findStudentById(studentId); // Validate student exists

    List<StudentEnrollment> enrollments = enrollmentRepository.findByStudentIdOrderByEnrollmentDateDesc(studentId);
    return enrollments.stream()
      .map(enrollmentMapper::toResponse)
      .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public PageResponse<StudentEnrollmentResponse> getClassEnrollments(Long classId, Pageable pageable) {
    findClassById(classId); // Validate class exists

    var enrollmentPage = enrollmentRepository.findActiveEnrollmentsByClassId(classId, pageable)
      .map(enrollmentMapper::toResponse);
    return PageResponse.from(enrollmentPage);
  }

  @Transactional(readOnly = true)
  public PageResponse<StudentEnrollmentResponse> getEnrollmentsByStatus(Long classId,
                                                                        StudentEnrollment.EnrollmentStatus status,
                                                                        Pageable pageable) {
    findClassById(classId); // Validate class exists

    var enrollmentPage = enrollmentRepository.findByClassIdAndStatus(classId, status, pageable)
      .map(enrollmentMapper::toResponse);
    return PageResponse.from(enrollmentPage);
  }

  @Transactional(readOnly = true)
  public PageResponse<StudentEnrollmentResponse> getEnrollmentsByDateRange(LocalDate startDate,
                                                                           LocalDate endDate,
                                                                           Pageable pageable) {
    if (startDate.isAfter(endDate)) {
      throw new BadRequestException("Start date cannot be after end date");
    }

    var enrollmentPage = enrollmentRepository.findByEnrollmentDateBetween(startDate, endDate, pageable)
      .map(enrollmentMapper::toResponse);
    return PageResponse.from(enrollmentPage);
  }

  @Transactional(readOnly = true)
  public PageResponse<StudentEnrollmentResponse> getSessionEnrollments(Long sessionId, Pageable pageable) {
    var enrollmentPage = enrollmentRepository.findActiveEnrollmentsBySessionId(sessionId, pageable)
      .map(enrollmentMapper::toResponse);
    return PageResponse.from(enrollmentPage);
  }

  @Transactional
  @Auditable(action = "COMPLETE_ENROLLMENT", entityType = "StudentEnrollment")
  public void completeEnrollment(Long enrollmentId) {
    StudentEnrollment enrollment = findEnrollmentById(enrollmentId);

    if (enrollment.getStatus() != StudentEnrollment.EnrollmentStatus.ACTIVE) {
      throw new BadRequestException("Only active enrollments can be completed");
    }

    enrollment.complete();
    enrollmentRepository.save(enrollment);

    log.info("Enrollment completed for student {} in class {}",
      enrollment.getStudent().getFullName(),
      enrollment.getSchoolClass().getDisplayName());
  }

  @Transactional
  @Auditable(action = "TRANSFER_ENROLLMENT", entityType = "StudentEnrollment")
  public void transferEnrollment(Long enrollmentId) {
    StudentEnrollment enrollment = findEnrollmentById(enrollmentId);

    if (enrollment.getStatus() != StudentEnrollment.EnrollmentStatus.ACTIVE) {
      throw new BadRequestException("Only active enrollments can be transferred");
    }

    enrollment.transfer();
    enrollmentRepository.save(enrollment);

    log.info("Enrollment transferred for student {} from class {}",
      enrollment.getStudent().getFullName(),
      enrollment.getSchoolClass().getDisplayName());
  }

  @Transactional(readOnly = true)
  public long getClassEnrollmentCount(Long classId) {
    return enrollmentRepository.countActiveEnrollmentsByClassId(classId);
  }

  @Transactional(readOnly = true)
  public boolean isStudentEnrolledInClass(Long studentId, Long classId) {
    return enrollmentRepository.existsByStudentIdAndSchoolClassIdAndStatus(
      studentId, classId, StudentEnrollment.EnrollmentStatus.ACTIVE);
  }

  private Student findStudentById(Long studentId) {
    return studentRepository.findById(studentId)
      .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));
  }

  private SchoolClass findClassById(Long classId) {
    return classRepository.findById(classId)
      .orElseThrow(() -> new ResourceNotFoundException("Class", "id", classId));
  }

  private StudentEnrollment findEnrollmentById(Long enrollmentId) {
    return enrollmentRepository.findById(enrollmentId)
      .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", enrollmentId));
  }

  private void validateEnrollment(Student student, SchoolClass schoolClass, LocalDate enrollmentDate) {
    // Check if student is active
    if (!student.isActive()) {
      throw new BadRequestException("Cannot enroll inactive student");
    }

    // Check if class is active
    if (!schoolClass.getIsActive()) {
      throw new BadRequestException("Cannot enroll in inactive class");
    }

    // Check enrollment date is not in future
    if (enrollmentDate.isAfter(LocalDate.now())) {
      throw new BadRequestException("Enrollment date cannot be in the future");
    }

    // Check class capacity
    long currentEnrollments = enrollmentRepository.countActiveEnrollmentsByClassId(schoolClass.getId());
    if (currentEnrollments >= schoolClass.getCapacity()) {
      throw new BadRequestException("Class has reached maximum capacity");
    }

    // Check if student is already enrolled in the same class
    if (enrollmentRepository.existsByStudentIdAndSchoolClassIdAndStatus(
      student.getId(), schoolClass.getId(), StudentEnrollment.EnrollmentStatus.ACTIVE)) {
      throw new BadRequestException("Student is already enrolled in this class");
    }

    // Check if enrollment date is after admission date
    if (enrollmentDate.isBefore(student.getAdmissionDate())) {
      throw new BadRequestException("Enrollment date cannot be before student's admission date");
    }
  }
}
