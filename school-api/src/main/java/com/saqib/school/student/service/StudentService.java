package com.saqib.school.student.service;

import com.saqib.school.academic.entity.SchoolClass;
import com.saqib.school.academic.repository.SchoolClassRepository;
import com.saqib.school.common.audit.Auditable;
import com.saqib.school.common.dto.PageResponse;
import com.saqib.school.common.exception.BadRequestException;
import com.saqib.school.common.exception.ResourceNotFoundException;
import com.saqib.school.student.entity.Student;
import com.saqib.school.student.entity.StudentEnrollment;
import com.saqib.school.student.entity.StudentGuardian;
import com.saqib.school.student.mapper.StudentMapper;
import com.saqib.school.student.model.StudentRequest;
import com.saqib.school.student.model.StudentResponse;
import com.saqib.school.student.model.StudentUpdateRequest;
import com.saqib.school.student.repository.StudentEnrollmentRepository;
import com.saqib.school.student.repository.StudentGuardianRepository;
import com.saqib.school.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentEnrollmentRepository enrollmentRepository;
    private final StudentGuardianRepository guardianRepository;
    private final SchoolClassRepository classRepository;
    private final StudentMapper studentMapper;
    private final StudentEnrollmentService enrollmentService;

    @Value("${app.student.registration-number.prefix:STD}")
    private String registrationPrefix;

    @Value("${app.student.registration-number.year-format:yy}")
    private String yearFormat;

    @Transactional
    @Auditable(action = "CREATE_STUDENT", entityType = "Student")
    public StudentResponse createStudent(StudentRequest request) {
        validateStudentRequest(request);

        SchoolClass schoolClass = findClassById(request.getClassId());
        validateClassCapacity(schoolClass);

        Student student = studentMapper.toEntity(request);
        student.setRegistrationNumber(generateRegistrationNumber());
        student.setStatus(Student.StudentStatus.ACTIVE);

        Student savedStudent = studentRepository.save(student);

        // Create initial enrollment
        enrollmentService.enrollStudent(savedStudent.getId(), request.getClassId(), request.getAdmissionDate());

        log.info("Student created successfully: {} with registration number: {}",
                savedStudent.getFullName(), savedStudent.getRegistrationNumber());

        return studentMapper.toResponse(savedStudent);
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long id) {
        Student student = findStudentById(id);
        return studentMapper.toResponse(student);
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudentByRegistrationNumber(String registrationNumber) {
        Student student = studentRepository.findByRegistrationNumber(registrationNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "registrationNumber", registrationNumber));
        return studentMapper.toResponse(student);
    }

    @Transactional(readOnly = true)
    public PageResponse<StudentResponse> getAllStudents(Pageable pageable) {
        var studentPage = studentRepository.findAll(pageable)
                .map(studentMapper::toResponse);
        return PageResponse.from(studentPage);
    }

    @Transactional(readOnly = true)
    public PageResponse<StudentResponse> getStudentsByStatus(Student.StudentStatus status, Pageable pageable) {
        var studentPage = studentRepository.findByStatus(status, pageable)
                .map(studentMapper::toResponse);
        return PageResponse.from(studentPage);
    }

    @Transactional(readOnly = true)
    public PageResponse<StudentResponse> searchStudents(String searchTerm, Pageable pageable) {
        var studentPage = studentRepository.findBySearchTerm(searchTerm, pageable)
                .map(studentMapper::toResponse);
        return PageResponse.from(studentPage);
    }

    @Transactional(readOnly = true)
    public PageResponse<StudentResponse> getStudentsByClass(Long classId, Pageable pageable) {
        var studentPage = studentRepository.findByActiveEnrollmentInClass(classId, pageable)
                .map(studentMapper::toResponse);
        return PageResponse.from(studentPage);
    }

    @Transactional(readOnly = true)
    public PageResponse<StudentResponse> getStudentsByAdmissionDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date");
        }

        var studentPage = studentRepository.findByAdmissionDateBetween(startDate, endDate, pageable)
                .map(studentMapper::toResponse);
        return PageResponse.from(studentPage);
    }

    @Transactional
    @Auditable(action = "UPDATE_STUDENT", entityType = "Student")
    public StudentResponse updateStudent(Long id, StudentUpdateRequest request) {
        Student student = findStudentById(id);

        validateStudentUpdateRequest(request, id);

        studentMapper.updateEntity(request, student);
        Student updatedStudent = studentRepository.save(student);

        log.info("Student updated successfully: {}", updatedStudent.getFullName());
        return studentMapper.toResponse(updatedStudent);
    }

    @Transactional
    @Auditable(action = "UPDATE_STUDENT_STATUS", entityType = "Student")
    public void updateStudentStatus(Long id, Student.StudentStatus status) {
        Student student = findStudentById(id);

        validateStatusTransition(student.getStatus(), status);

        student.setStatus(status);

        // Handle status-specific logic
        if (status == Student.StudentStatus.TRANSFERRED || status == Student.StudentStatus.GRADUATED) {
            // Complete active enrollments
            enrollmentRepository.findActiveEnrollmentByStudentId(id)
                    .ifPresent(enrollment -> {
                        enrollment.complete();
                        enrollmentRepository.save(enrollment);
                    });
        }

        studentRepository.save(student);
        log.info("Student status updated to {} for student: {}", status, student.getFullName());
    }

    @Transactional
    @Auditable(action = "TRANSFER_STUDENT", entityType = "Student")
    public void transferStudent(Long studentId, Long newClassId, LocalDate transferDate) {
        Student student = findStudentById(studentId);
        SchoolClass newClass = findClassById(newClassId);

        if (!student.isActive()) {
            throw new BadRequestException("Cannot transfer inactive student");
        }

        validateClassCapacity(newClass);

        // Complete current enrollment
        StudentEnrollment currentEnrollment = enrollmentRepository.findActiveEnrollmentByStudentId(studentId)
                .orElseThrow(() -> new BadRequestException("Student has no active enrollment"));

        currentEnrollment.transfer();
        enrollmentRepository.save(currentEnrollment);

        // Create new enrollment
        enrollmentService.enrollStudent(studentId, newClassId, transferDate);

        log.info("Student {} transferred from class {} to class {}",
                student.getFullName(),
                currentEnrollment.getSchoolClass().getDisplayName(),
                newClass.getDisplayName());
    }

    @Transactional(readOnly = true)
    public long getTotalActiveStudents() {
        return studentRepository.countActiveStudents();
    }

    @Transactional(readOnly = true)
    public long getStudentCountInClass(Long classId) {
        return studentRepository.countStudentsInClass(classId);
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getStudentsBirthdayToday() {
        List<Student> students = studentRepository.findByDateOfBirth(LocalDate.now());
        return students.stream()
                .map(studentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> findDuplicateStudents(String phone, String email) {
        List<Student> students = studentRepository.findByPhoneOrEmail(phone, email);
        return students.stream()
                .map(studentMapper::toResponse)
                .collect(Collectors.toList());
    }

    private Student findStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
    }

    private SchoolClass findClassById(Long classId) {
        return classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class", "id", classId));
    }

    private void validateStudentRequest(StudentRequest request) {
        // Check for duplicate phone/email
        if (request.getPhone() != null || request.getEmail() != null) {
            List<Student> duplicates = studentRepository.findByPhoneOrEmail(request.getPhone(), request.getEmail());
            if (!duplicates.isEmpty()) {
                throw new BadRequestException("Student with same phone or email already exists");
            }
        }

        // Validate age constraints
        if (request.getDateOfBirth().isAfter(LocalDate.now().minusYears(3))) {
            throw new BadRequestException("Student must be at least 3 years old");
        }

        if (request.getDateOfBirth().isBefore(LocalDate.now().minusYears(25))) {
            throw new BadRequestException("Student cannot be older than 25 years");
        }
    }

    private void validateStudentUpdateRequest(StudentUpdateRequest request, Long studentId) {
        if (request.getPhone() != null || request.getEmail() != null) {
            List<Student> duplicates = studentRepository.findByPhoneOrEmail(request.getPhone(), request.getEmail());
            boolean hasDuplicates = duplicates.stream()
                    .anyMatch(student -> !student.getId().equals(studentId));

            if (hasDuplicates) {
                throw new BadRequestException("Student with same phone or email already exists");
            }
        }
    }

    private void validateClassCapacity(SchoolClass schoolClass) {
        long currentCount = studentRepository.countStudentsInClass(schoolClass.getId());
        if (currentCount >= schoolClass.getCapacity()) {
            throw new BadRequestException("Class " + schoolClass.getDisplayName() + " has reached maximum capacity");
        }
    }

    private void validateStatusTransition(Student.StudentStatus currentStatus, Student.StudentStatus newStatus) {
        // Define valid status transitions
        boolean isValidTransition = switch (currentStatus) {
            case ACTIVE -> newStatus == Student.StudentStatus.INACTIVE ||
                         newStatus == Student.StudentStatus.TRANSFERRED ||
                         newStatus == Student.StudentStatus.GRADUATED;
            case INACTIVE -> newStatus == Student.StudentStatus.ACTIVE ||
                           newStatus == Student.StudentStatus.TRANSFERRED;
            case TRANSFERRED, GRADUATED -> false; // Final states
        };

        if (!isValidTransition) {
            throw new BadRequestException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }
    }

    private String generateRegistrationNumber() {
        String year = LocalDate.now().format(DateTimeFormatter.ofPattern(yearFormat));
        long count = studentRepository.count() + 1;
        return String.format("%s%s%04d", registrationPrefix, year, count);
    }
}
