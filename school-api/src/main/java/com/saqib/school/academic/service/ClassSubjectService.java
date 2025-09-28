package com.saqib.school.academic.service;

import com.saqib.school.academic.entity.ClassSubject;
import com.saqib.school.academic.entity.SchoolClass;
import com.saqib.school.academic.entity.Subject;
import com.saqib.school.academic.mapper.ClassSubjectMapper;
import com.saqib.school.academic.model.ClassSubjectRequest;
import com.saqib.school.academic.model.ClassSubjectResponse;
import com.saqib.school.academic.repository.ClassSubjectRepository;
import com.saqib.school.academic.repository.SchoolClassRepository;
import com.saqib.school.academic.repository.SubjectRepository;
import com.saqib.school.common.audit.Auditable;
import com.saqib.school.common.exception.BadRequestException;
import com.saqib.school.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClassSubjectService {

  private final ClassSubjectRepository classSubjectRepository;
  private final SchoolClassRepository schoolClassRepository;
  private final SubjectRepository subjectRepository;
  private final ClassSubjectMapper classSubjectMapper;

  @Transactional
  @Auditable(action = "ASSIGN_SUBJECT_TO_CLASS", entityType = "ClassSubject")
  public ClassSubjectResponse assignSubjectToClass(ClassSubjectRequest request) {
    validateClassSubjectRequest(request);

    SchoolClass schoolClass = findClassById(request.getClassId());
    Subject subject = findSubjectById(request.getSubjectId());

    if (classSubjectRepository.existsBySchoolClassIdAndSubjectId(request.getClassId(), request.getSubjectId())) {
      throw new BadRequestException("Subject '" + subject.getSubjectName() +
        "' is already assigned to class " + schoolClass.getDisplayName());
    }

    ClassSubject classSubject = classSubjectMapper.toEntity(request);
    classSubject.setSchoolClass(schoolClass);
    classSubject.setSubject(subject);

    // Set defaults if not provided
    if (classSubject.getTotalMarks() == null) {
      classSubject.setTotalMarks(100);
    }
    if (classSubject.getPassingMarks() == null) {
      classSubject.setPassingMarks(40);
    }

    ClassSubject savedClassSubject = classSubjectRepository.save(classSubject);
    log.info("Subject '{}' assigned to class '{}' successfully",
      subject.getSubjectName(), schoolClass.getDisplayName());

    return classSubjectMapper.toResponse(savedClassSubject);
  }

  @Transactional(readOnly = true)
  public ClassSubjectResponse getClassSubjectById(Long id) {
    ClassSubject classSubject = findClassSubjectById(id);
    return classSubjectMapper.toResponse(classSubject);
  }

  @Transactional(readOnly = true)
  public List<ClassSubjectResponse> getSubjectsByClassId(Long classId) {
    validateClassExists(classId);

    return classSubjectRepository.findByClassId(classId)
      .stream()
      .map(classSubjectMapper::toResponse)
      .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<ClassSubjectResponse> getClassesBySubjectId(Long subjectId) {
    validateSubjectExists(subjectId);

    return classSubjectRepository.findBySubjectId(subjectId)
      .stream()
      .map(classSubjectMapper::toResponse)
      .collect(Collectors.toList());
  }

  @Transactional
  @Auditable(action = "UPDATE_CLASS_SUBJECT", entityType = "ClassSubject")
  public ClassSubjectResponse updateClassSubject(Long id, ClassSubjectRequest request) {
    ClassSubject existingClassSubject = findClassSubjectById(id);
    validateClassSubjectRequest(request);

    // Check if the update would create a duplicate mapping
    if ((!existingClassSubject.getSchoolClass().getId().equals(request.getClassId()) ||
      !existingClassSubject.getSubject().getId().equals(request.getSubjectId())) &&
      classSubjectRepository.existsBySchoolClassIdAndSubjectId(request.getClassId(), request.getSubjectId())) {

      SchoolClass newClass = findClassById(request.getClassId());
      Subject newSubject = findSubjectById(request.getSubjectId());
      throw new BadRequestException("Subject '" + newSubject.getSubjectName() +
        "' is already assigned to class " + newClass.getDisplayName());
    }

    SchoolClass schoolClass = findClassById(request.getClassId());
    Subject subject = findSubjectById(request.getSubjectId());

    classSubjectMapper.updateEntity(request, existingClassSubject);
    existingClassSubject.setSchoolClass(schoolClass);
    existingClassSubject.setSubject(subject);

    ClassSubject updatedClassSubject = classSubjectRepository.save(existingClassSubject);
    log.info("Class-Subject mapping updated successfully: {} - {}",
      schoolClass.getDisplayName(), subject.getSubjectName());

    return classSubjectMapper.toResponse(updatedClassSubject);
  }

  @Transactional
  @Auditable(action = "REMOVE_SUBJECT_FROM_CLASS", entityType = "ClassSubject")
  public void removeSubjectFromClass(Long id) {
    ClassSubject classSubject = findClassSubjectById(id);

    String className = classSubject.getSchoolClass().getDisplayName();
    String subjectName = classSubject.getSubject().getSubjectName();

    classSubjectRepository.delete(classSubject);
    log.info("Subject '{}' removed from class '{}' successfully", subjectName, className);
  }

  @Transactional
  @Auditable(action = "BULK_ASSIGN_SUBJECTS", entityType = "ClassSubject")
  public List<ClassSubjectResponse> bulkAssignSubjectsToClass(Long classId, List<Long> subjectIds) {
    SchoolClass schoolClass = findClassById(classId);

    List<ClassSubjectResponse> results = subjectIds.stream()
      .map(subjectId -> {
        if (!classSubjectRepository.existsBySchoolClassIdAndSubjectId(classId, subjectId)) {
          Subject subject = findSubjectById(subjectId);

          ClassSubject classSubject = ClassSubject.builder()
            .schoolClass(schoolClass)
            .subject(subject)
            .totalMarks(100)
            .passingMarks(40)
            .build();

          ClassSubject saved = classSubjectRepository.save(classSubject);
          return classSubjectMapper.toResponse(saved);
        }
        return null;
      })
      .filter(response -> response != null)
      .collect(Collectors.toList());

    log.info("Bulk assigned {} subjects to class '{}'", results.size(), schoolClass.getDisplayName());
    return results;
  }

  @Transactional
  @Auditable(action = "COPY_SUBJECTS_TO_CLASS", entityType = "ClassSubject")
  public List<ClassSubjectResponse> copySubjectsFromClass(Long sourceClassId, Long targetClassId) {
    SchoolClass sourceClass = findClassById(sourceClassId);
    SchoolClass targetClass = findClassById(targetClassId);

    List<ClassSubject> sourceClassSubjects = classSubjectRepository.findByClassId(sourceClassId);

    List<ClassSubjectResponse> results = sourceClassSubjects.stream()
      .filter(cs -> !classSubjectRepository.existsBySchoolClassIdAndSubjectId(
        targetClassId, cs.getSubject().getId()))
      .map(sourceCs -> {
        ClassSubject newClassSubject = ClassSubject.builder()
          .schoolClass(targetClass)
          .subject(sourceCs.getSubject())
          .totalMarks(sourceCs.getTotalMarks())
          .passingMarks(sourceCs.getPassingMarks())
          .build();

        ClassSubject saved = classSubjectRepository.save(newClassSubject);
        return classSubjectMapper.toResponse(saved);
      })
      .collect(Collectors.toList());

    log.info("Copied {} subjects from class '{}' to class '{}'",
      results.size(), sourceClass.getDisplayName(), targetClass.getDisplayName());
    return results;
  }

  private ClassSubject findClassSubjectById(Long id) {
    return classSubjectRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Class-Subject mapping", "id", id));
  }

  private SchoolClass findClassById(Long classId) {
    return schoolClassRepository.findById(classId)
      .orElseThrow(() -> new ResourceNotFoundException("Class", "id", classId));
  }

  private Subject findSubjectById(Long subjectId) {
    return subjectRepository.findById(subjectId)
      .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", subjectId));
  }

  private void validateClassExists(Long classId) {
    if (!schoolClassRepository.existsById(classId)) {
      throw new ResourceNotFoundException("Class", "id", classId);
    }
  }

  private void validateSubjectExists(Long subjectId) {
    if (!subjectRepository.existsById(subjectId)) {
      throw new ResourceNotFoundException("Subject", "id", subjectId);
    }
  }

  private void validateClassSubjectRequest(ClassSubjectRequest request) {
    if (request.getTotalMarks() != null && request.getPassingMarks() != null) {
      if (request.getPassingMarks() > request.getTotalMarks()) {
        throw new BadRequestException("Passing marks cannot be greater than total marks");
      }
    }

    if (request.getTotalMarks() != null && (request.getTotalMarks() < 1 || request.getTotalMarks() > 1000)) {
      throw new BadRequestException("Total marks must be between 1 and 1000");
    }

    if (request.getPassingMarks() != null && request.getPassingMarks() < 0) {
      throw new BadRequestException("Passing marks cannot be negative");
    }
  }
}
