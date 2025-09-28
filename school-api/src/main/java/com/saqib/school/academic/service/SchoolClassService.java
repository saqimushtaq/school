package com.saqib.school.academic.service;

import com.saqib.school.academic.entity.AcademicSession;
import com.saqib.school.academic.entity.SchoolClass;
import com.saqib.school.academic.mapper.SchoolClassMapper;
import com.saqib.school.academic.model.SchoolClassRequest;
import com.saqib.school.academic.model.SchoolClassResponse;
import com.saqib.school.academic.repository.AcademicSessionRepository;
import com.saqib.school.academic.repository.SchoolClassRepository;
import com.saqib.school.common.audit.Auditable;
import com.saqib.school.common.dto.PageResponse;
import com.saqib.school.common.exception.BadRequestException;
import com.saqib.school.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchoolClassService {

    private final SchoolClassRepository classRepository;
    private final AcademicSessionRepository sessionRepository;
    private final SchoolClassMapper classMapper;

    @Transactional
    @Auditable(action = "CREATE_CLASS", entityType = "SchoolClass")
    public SchoolClassResponse createClass(SchoolClassRequest request) {
        validateClassRequest(request);

        AcademicSession session = findSessionById(request.getSessionId());

        // Set default section if not provided
        String section = request.getSection() != null ? request.getSection() : "A";

        if (classRepository.existsBySessionIdAndClassNameAndSection(
                request.getSessionId(), request.getClassName(), section)) {
            throw new BadRequestException("Class " + request.getClassName() +
                    " section " + section + " already exists in this session");
        }

        SchoolClass schoolClass = classMapper.toEntity(request);
        schoolClass.setSession(session);
        schoolClass.setSection(section);
        schoolClass.setIsActive(true);

        // Set default capacity if not provided
        if (schoolClass.getCapacity() == null) {
            schoolClass.setCapacity(30);
        }

        SchoolClass savedClass = classRepository.save(schoolClass);
        log.info("Class created successfully: {} - {}", savedClass.getClassName(), savedClass.getSection());

        return classMapper.toResponse(savedClass);
    }

    @Transactional(readOnly = true)
    public SchoolClassResponse getClassById(Long id) {
        SchoolClass schoolClass = findClassById(id);
        return classMapper.toResponse(schoolClass);
    }

    @Transactional(readOnly = true)
    public PageResponse<SchoolClassResponse> getAllClasses(Pageable pageable) {
        var classPage = classRepository.findAll(pageable).map(classMapper::toResponse);
        return PageResponse.from(classPage);
    }

    @Transactional(readOnly = true)
    public PageResponse<SchoolClassResponse> getClassesBySession(Long sessionId, Pageable pageable) {
        validateSessionExists(sessionId);
        var classPage = classRepository.findBySessionId(sessionId, pageable).map(classMapper::toResponse);
        return PageResponse.from(classPage);
    }

    @Transactional(readOnly = true)
    public List<SchoolClassResponse> getActiveClassesBySession(Long sessionId) {
        validateSessionExists(sessionId);

        List<SchoolClass> activeClasses = classRepository.findActiveClassesBySessionId(sessionId);
        return activeClasses.stream()
                .map(classMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Auditable(action = "UPDATE_CLASS", entityType = "SchoolClass")
    public SchoolClassResponse updateClass(Long id, SchoolClassRequest request) {
        SchoolClass existingClass = findClassById(id);
        validateClassRequest(request);

        // Check if the update would create a duplicate
        String newSection = request.getSection() != null ? request.getSection() : existingClass.getSection();

        if ((!existingClass.getSession().getId().equals(request.getSessionId()) ||
             !existingClass.getClassName().equals(request.getClassName()) ||
             !existingClass.getSection().equals(newSection)) &&
            classRepository.existsBySessionIdAndClassNameAndSection(
                    request.getSessionId(), request.getClassName(), newSection)) {
            throw new BadRequestException("Class " + request.getClassName() +
                    " section " + newSection + " already exists in this session");
        }

        AcademicSession session = findSessionById(request.getSessionId());

        classMapper.updateEntity(request, existingClass);
        existingClass.setSession(session);
        existingClass.setSection(newSection);

        SchoolClass updatedClass = classRepository.save(existingClass);
        log.info("Class updated successfully: {} - {}", updatedClass.getClassName(), updatedClass.getSection());

        return classMapper.toResponse(updatedClass);
    }

    @Transactional
    @Auditable(action = "ACTIVATE_CLASS", entityType = "SchoolClass")
    public void activateClass(Long id) {
        SchoolClass schoolClass = findClassById(id);
        schoolClass.setIsActive(true);
        classRepository.save(schoolClass);

        log.info("Class activated: {} - {}", schoolClass.getClassName(), schoolClass.getSection());
    }

    @Transactional
    @Auditable(action = "DEACTIVATE_CLASS", entityType = "SchoolClass")
    public void deactivateClass(Long id) {
        SchoolClass schoolClass = findClassById(id);
        schoolClass.setIsActive(false);
        classRepository.save(schoolClass);

        log.info("Class deactivated: {} - {}", schoolClass.getClassName(), schoolClass.getSection());
    }

    @Transactional
    @Auditable(action = "DELETE_CLASS", entityType = "SchoolClass")
    public void deleteClass(Long id) {
        SchoolClass schoolClass = findClassById(id);

        // Check if class has any subjects assigned
        if (schoolClass.getClassSubjects() != null && !schoolClass.getClassSubjects().isEmpty()) {
            throw new BadRequestException("Cannot delete class that has subjects assigned to it");
        }

        classRepository.delete(schoolClass);
        log.info("Class deleted successfully: {} - {}", schoolClass.getClassName(), schoolClass.getSection());
    }

    private SchoolClass findClassById(Long id) {
        return classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class", "id", id));
    }

    private AcademicSession findSessionById(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Academic session", "id", sessionId));
    }

    private void validateSessionExists(Long sessionId) {
        if (!sessionRepository.existsById(sessionId)) {
            throw new ResourceNotFoundException("Academic session", "id", sessionId);
        }
    }

    private void validateClassRequest(SchoolClassRequest request) {
        if (request.getCapacity() != null && request.getCapacity() < 1) {
            throw new BadRequestException("Class capacity must be at least 1");
        }

        if (request.getCapacity() != null && request.getCapacity() > 100) {
            throw new BadRequestException("Class capacity cannot exceed 100");
        }
    }
}
