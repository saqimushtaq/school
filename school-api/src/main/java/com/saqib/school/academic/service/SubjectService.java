package com.saqib.school.academic.service;

import com.saqib.school.academic.entity.Subject;
import com.saqib.school.academic.mapper.SubjectMapper;
import com.saqib.school.academic.model.SubjectRequest;
import com.saqib.school.academic.model.SubjectResponse;
import com.saqib.school.academic.repository.SubjectRepository;
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
public class SubjectService {

  private final SubjectRepository subjectRepository;
  private final SubjectMapper subjectMapper;

  @Transactional
  @Auditable(action = "CREATE_SUBJECT", entityType = "Subject")
  public SubjectResponse createSubject(SubjectRequest request) {
    if (subjectRepository.existsBySubjectName(request.getSubjectName())) {
      throw new BadRequestException("Subject with name '" + request.getSubjectName() + "' already exists");
    }

    if (request.getSubjectCode() != null &&
      subjectRepository.existsBySubjectCode(request.getSubjectCode())) {
      throw new BadRequestException("Subject with code '" + request.getSubjectCode() + "' already exists");
    }

    Subject subject = subjectMapper.toEntity(request);
    subject.setIsActive(true);

    Subject savedSubject = subjectRepository.save(subject);
    log.info("Subject created successfully: {}", savedSubject.getSubjectName());

    return subjectMapper.toResponse(savedSubject);
  }

  @Transactional(readOnly = true)
  public SubjectResponse getSubjectById(Long id) {
    Subject subject = findSubjectById(id);
    return subjectMapper.toResponse(subject);
  }

  @Transactional(readOnly = true)
  public SubjectResponse getSubjectByName(String subjectName) {
    Subject subject = subjectRepository.findBySubjectName(subjectName)
      .orElseThrow(() -> new ResourceNotFoundException("Subject", "subjectName", subjectName));
    return subjectMapper.toResponse(subject);
  }

  @Transactional(readOnly = true)
  public SubjectResponse getSubjectByCode(String subjectCode) {
    Subject subject = subjectRepository.findBySubjectCode(subjectCode)
      .orElseThrow(() -> new ResourceNotFoundException("Subject", "subjectCode", subjectCode));
    return subjectMapper.toResponse(subject);
  }

  @Transactional(readOnly = true)
  public PageResponse<SubjectResponse> getAllSubjects(Pageable pageable) {
    var subjectPage = subjectRepository.findAll(pageable).map(subjectMapper::toResponse);
    return PageResponse.from(subjectPage);
  }

  @Transactional(readOnly = true)
  public List<SubjectResponse> getActiveSubjects() {
    return subjectRepository.findActiveSubjects()
      .stream()
      .map(subjectMapper::toResponse)
      .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public PageResponse<SubjectResponse> getSubjectsByStatus(Boolean isActive, Pageable pageable) {
    var subjectPage = subjectRepository.findByIsActive(isActive, pageable).map(subjectMapper::toResponse);
    return PageResponse.from(subjectPage);
  }

  @Transactional
  @Auditable(action = "UPDATE_SUBJECT", entityType = "Subject")
  public SubjectResponse updateSubject(Long id, SubjectRequest request) {
    Subject subject = findSubjectById(id);

    // Check if subject name is being changed and if new name already exists
    if (!subject.getSubjectName().equals(request.getSubjectName()) &&
      subjectRepository.existsBySubjectName(request.getSubjectName())) {
      throw new BadRequestException("Subject with name '" + request.getSubjectName() + "' already exists");
    }

    // Check if subject code is being changed and if new code already exists
    if (request.getSubjectCode() != null &&
      !request.getSubjectCode().equals(subject.getSubjectCode()) &&
      subjectRepository.existsBySubjectCode(request.getSubjectCode())) {
      throw new BadRequestException("Subject with code '" + request.getSubjectCode() + "' already exists");
    }

    subjectMapper.updateEntity(request, subject);
    Subject updatedSubject = subjectRepository.save(subject);

    log.info("Subject updated successfully: {}", updatedSubject.getSubjectName());
    return subjectMapper.toResponse(updatedSubject);
  }

  @Transactional
  @Auditable(action = "ACTIVATE_SUBJECT", entityType = "Subject")
  public void activateSubject(Long id) {
    Subject subject = findSubjectById(id);
    subject.setIsActive(true);
    subjectRepository.save(subject);

    log.info("Subject activated: {}", subject.getSubjectName());
  }

  @Transactional
  @Auditable(action = "DEACTIVATE_SUBJECT", entityType = "Subject")
  public void deactivateSubject(Long id) {
    Subject subject = findSubjectById(id);
    subject.setIsActive(false);
    subjectRepository.save(subject);

    log.info("Subject deactivated: {}", subject.getSubjectName());
  }

  @Transactional
  @Auditable(action = "DELETE_SUBJECT", entityType = "Subject")
  public void deleteSubject(Long id) {
    Subject subject = findSubjectById(id);

    // Check if subject is assigned to any classes
    if (subject.getClassSubjects() != null && !subject.getClassSubjects().isEmpty()) {
      throw new BadRequestException("Cannot delete subject that is assigned to classes");
    }

    subjectRepository.delete(subject);
    log.info("Subject deleted successfully: {}", subject.getSubjectName());
  }

  private Subject findSubjectById(Long id) {
    return subjectRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", id));
  }
}
