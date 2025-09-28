package com.saqib.school.student.service;

import com.saqib.school.common.audit.Auditable;
import com.saqib.school.common.exception.BadRequestException;
import com.saqib.school.common.exception.ResourceNotFoundException;
import com.saqib.school.student.entity.Student;
import com.saqib.school.student.entity.StudentGuardian;
import com.saqib.school.student.mapper.StudentGuardianMapper;
import com.saqib.school.student.model.StudentGuardianRequest;
import com.saqib.school.student.model.StudentGuardianResponse;
import com.saqib.school.student.repository.StudentGuardianRepository;
import com.saqib.school.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentGuardianService {

    private final StudentGuardianRepository guardianRepository;
    private final StudentRepository studentRepository;
    private final StudentGuardianMapper guardianMapper;

    @Transactional
    @Auditable(action = "ADD_GUARDIAN", entityType = "StudentGuardian")
    public StudentGuardianResponse addGuardian(Long studentId, StudentGuardianRequest request) {
        Student student = findStudentById(studentId);

        validateGuardianRequest(studentId, request);

        StudentGuardian guardian = guardianMapper.toEntity(request);
        guardian.setStudent(student);

        // Handle primary contact logic
        if (request.getIsPrimaryContact()) {
            clearExistingPrimaryContact(studentId);
        }

        StudentGuardian savedGuardian = guardianRepository.save(guardian);

        log.info("Guardian {} added for student: {}",
                savedGuardian.getName(), student.getFullName());

        return guardianMapper.toResponse(savedGuardian);
    }

    @Transactional(readOnly = true)
    public List<StudentGuardianResponse> getStudentGuardians(Long studentId) {
        findStudentById(studentId); // Validate student exists

        List<StudentGuardian> guardians = guardianRepository.findByStudentId(studentId);
        return guardians.stream()
                .map(guardianMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StudentGuardianResponse getGuardianById(Long guardianId) {
        StudentGuardian guardian = findGuardianById(guardianId);
        return guardianMapper.toResponse(guardian);
    }

    @Transactional(readOnly = true)
    public StudentGuardianResponse getPrimaryContact(Long studentId) {
        findStudentById(studentId); // Validate student exists

        StudentGuardian primaryContact = guardianRepository.findPrimaryContactByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Primary contact not found for student"));

        return guardianMapper.toResponse(primaryContact);
    }

    @Transactional
    @Auditable(action = "UPDATE_GUARDIAN", entityType = "StudentGuardian")
    public StudentGuardianResponse updateGuardian(Long guardianId, StudentGuardianRequest request) {
        StudentGuardian guardian = findGuardianById(guardianId);

        validateGuardianUpdateRequest(guardian.getStudent().getId(), request, guardianId);

        // Handle primary contact change
        if (request.getIsPrimaryContact() && !guardian.getIsPrimaryContact()) {
            clearExistingPrimaryContact(guardian.getStudent().getId());
        }

        guardianMapper.updateEntity(request, guardian);
        StudentGuardian updatedGuardian = guardianRepository.save(guardian);

        log.info("Guardian {} updated for student: {}",
                updatedGuardian.getName(), guardian.getStudent().getFullName());

        return guardianMapper.toResponse(updatedGuardian);
    }

    @Transactional
    @Auditable(action = "SET_PRIMARY_CONTACT", entityType = "StudentGuardian")
    public void setPrimaryContact(Long guardianId) {
        StudentGuardian guardian = findGuardianById(guardianId);

        if (guardian.getIsPrimaryContact()) {
            throw new BadRequestException("Guardian is already the primary contact");
        }

        clearExistingPrimaryContact(guardian.getStudent().getId());

        guardian.setIsPrimaryContact(true);
        guardianRepository.save(guardian);

        log.info("Guardian {} set as primary contact for student: {}",
                guardian.getName(), guardian.getStudent().getFullName());
    }

    @Transactional
    @Auditable(action = "DELETE_GUARDIAN", entityType = "StudentGuardian")
    public void deleteGuardian(Long guardianId) {
        StudentGuardian guardian = findGuardianById(guardianId);

        // Ensure at least one guardian remains
        List<StudentGuardian> allGuardians = guardianRepository.findByStudentId(guardian.getStudent().getId());
        if (allGuardians.size() <= 1) {
            throw new BadRequestException("Cannot delete the last guardian. Student must have at least one guardian.");
        }

        // If deleting primary contact, assign another as primary
        if (guardian.getIsPrimaryContact()) {
            StudentGuardian newPrimary = allGuardians.stream()
                    .filter(g -> !g.getId().equals(guardianId))
                    .findFirst()
                    .orElseThrow();

            newPrimary.setIsPrimaryContact(true);
            guardianRepository.save(newPrimary);
        }

        guardianRepository.delete(guardian);

        log.info("Guardian {} deleted for student: {}",
                guardian.getName(), guardian.getStudent().getFullName());
    }

    @Transactional(readOnly = true)
    public List<StudentGuardianResponse> findGuardiansByContact(String phone, String email) {
        List<StudentGuardian> guardians = guardianRepository.findByPhoneOrEmail(phone, email);
        return guardians.stream()
                .map(guardianMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StudentGuardianResponse> findGuardiansByCnic(String cnic) {
        List<StudentGuardian> guardians = guardianRepository.findByCnic(cnic);
        return guardians.stream()
                .map(guardianMapper::toResponse)
                .collect(Collectors.toList());
    }

    private Student findStudentById(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));
    }

    private StudentGuardian findGuardianById(Long guardianId) {
        return guardianRepository.findById(guardianId)
                .orElseThrow(() -> new ResourceNotFoundException("Guardian", "id", guardianId));
    }

    private void validateGuardianRequest(Long studentId, StudentGuardianRequest request) {
        // Check for duplicate guardian type for same student
        if (guardianRepository.findByStudentIdAndGuardianType(studentId, request.getGuardianType()).isPresent()) {
            throw new BadRequestException("Guardian of type " + request.getGuardianType() + " already exists for this student");
        }

        // Check for duplicate contact information
        if (request.getPhone() != null || request.getEmail() != null) {
            List<StudentGuardian> duplicates = guardianRepository.findByPhoneOrEmail(request.getPhone(), request.getEmail());
            if (!duplicates.isEmpty()) {
                throw new BadRequestException("Guardian with same phone or email already exists");
            }
        }

        // Check for duplicate CNIC
        if (request.getCnic() != null) {
            List<StudentGuardian> duplicates = guardianRepository.findByCnic(request.getCnic());
            if (!duplicates.isEmpty()) {
                throw new BadRequestException("Guardian with same CNIC already exists");
            }
        }
    }

    private void validateGuardianUpdateRequest(Long studentId, StudentGuardianRequest request, Long guardianId) {
        // Check for duplicate contact information (excluding current guardian)
        if (request.getPhone() != null || request.getEmail() != null) {
            List<StudentGuardian> duplicates = guardianRepository.findByPhoneOrEmail(request.getPhone(), request.getEmail());
            boolean hasDuplicates = duplicates.stream()
                    .anyMatch(guardian -> !guardian.getId().equals(guardianId));

            if (hasDuplicates) {
                throw new BadRequestException("Guardian with same phone or email already exists");
            }
        }

        // Check for duplicate CNIC (excluding current guardian)
        if (request.getCnic() != null) {
            List<StudentGuardian> duplicates = guardianRepository.findByCnic(request.getCnic());
            boolean hasDuplicates = duplicates.stream()
                    .anyMatch(guardian -> !guardian.getId().equals(guardianId));

            if (hasDuplicates) {
                throw new BadRequestException("Guardian with same CNIC already exists");
            }
        }
    }

    private void clearExistingPrimaryContact(Long studentId) {
        guardianRepository.findPrimaryContactByStudentId(studentId)
                .ifPresent(existingPrimary -> {
                    existingPrimary.setIsPrimaryContact(false);
                    guardianRepository.save(existingPrimary);
                });
    }
}
