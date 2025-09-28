package com.saqib.school.academic.service;

import com.saqib.school.academic.entity.AcademicSession;
import com.saqib.school.academic.mapper.AcademicSessionMapper;
import com.saqib.school.academic.model.AcademicSessionRequest;
import com.saqib.school.academic.model.AcademicSessionResponse;
import com.saqib.school.academic.repository.AcademicSessionRepository;
import com.saqib.school.common.audit.Auditable;
import com.saqib.school.common.dto.PageResponse;
import com.saqib.school.common.exception.BadRequestException;
import com.saqib.school.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class AcademicSessionService {

  private final AcademicSessionRepository sessionRepository;
  private final AcademicSessionMapper sessionMapper;

  @Transactional
  @Auditable(action = "CREATE_ACADEMIC_SESSION", entityType = "AcademicSession")
  public AcademicSessionResponse createSession(AcademicSessionRequest request) {
    validateSessionRequest(request);

    if (sessionRepository.existsBySessionName(request.getSessionName())) {
      throw new BadRequestException("Academic session with name '" + request.getSessionName() + "' already exists");
    }

    AcademicSession session = sessionMapper.toEntity(request);
    session.setStatus(AcademicSession.SessionStatus.UPCOMING);

    AcademicSession savedSession = sessionRepository.save(session);
    log.info("Academic session created successfully: {}", savedSession.getSessionName());

    return sessionMapper.toResponse(savedSession);
  }

  @Transactional(readOnly = true)
  public AcademicSessionResponse getSessionById(Long id) {
    AcademicSession session = findSessionById(id);
    return sessionMapper.toResponse(session);
  }

  @Transactional(readOnly = true)
  public AcademicSessionResponse getSessionByName(String sessionName) {
    AcademicSession session = sessionRepository.findBySessionName(sessionName)
      .orElseThrow(() -> new ResourceNotFoundException("Academic session", "sessionName", sessionName));
    return sessionMapper.toResponse(session);
  }

  @Transactional(readOnly = true)
  public PageResponse<AcademicSessionResponse> getAllSessions(Pageable pageable) {
    var sessionPage = sessionRepository.findAll(pageable).map(sessionMapper::toResponse);
    return PageResponse.from(sessionPage);
  }

  @Transactional(readOnly = true)
  public PageResponse<AcademicSessionResponse> getSessionsByStatus(AcademicSession.SessionStatus status, Pageable pageable) {
    var sessionPage = sessionRepository.findByStatus(status, pageable).map(sessionMapper::toResponse);
    return PageResponse.from(sessionPage);
  }

  @Transactional(readOnly = true)
  public AcademicSessionResponse getActiveSession() {
    AcademicSession activeSession = sessionRepository.findActiveSession()
      .orElseThrow(() -> new ResourceNotFoundException("No active academic session found"));
    return sessionMapper.toResponse(activeSession);
  }

  @Transactional(readOnly = true)
  public AcademicSessionResponse getUpcomingSession() {
    AcademicSession upcomingSession = sessionRepository.findUpcomingSession()
      .orElseThrow(() -> new ResourceNotFoundException("No upcoming academic session found"));
    return sessionMapper.toResponse(upcomingSession);
  }

  @Transactional
  @Auditable(action = "UPDATE_ACADEMIC_SESSION", entityType = "AcademicSession")
  public AcademicSessionResponse updateSession(Long id, AcademicSessionRequest request) {
    AcademicSession session = findSessionById(id);
    validateSessionRequest(request);

    // Check if session name is being changed and if new name already exists
    if (!session.getSessionName().equals(request.getSessionName()) &&
      sessionRepository.existsBySessionName(request.getSessionName())) {
      throw new BadRequestException("Academic session with name '" + request.getSessionName() + "' already exists");
    }

    // Prevent updates to active sessions except for end date extension
    if (session.getStatus() == AcademicSession.SessionStatus.ACTIVE) {
      if (!session.getSessionName().equals(request.getSessionName()) ||
        !session.getStartDate().equals(request.getStartDate()) ||
        request.getEndDate().isBefore(session.getEndDate())) {
        throw new BadRequestException("Cannot modify active session details except extending end date");
      }
    }

    sessionMapper.updateEntity(request, session);
    AcademicSession updatedSession = sessionRepository.save(session);

    log.info("Academic session updated successfully: {}", updatedSession.getSessionName());
    return sessionMapper.toResponse(updatedSession);
  }

  @Transactional
  @Auditable(action = "ACTIVATE_SESSION", entityType = "AcademicSession")
  public void activateSession(Long id) {
    AcademicSession session = findSessionById(id);

    if (!session.canBeActivated()) {
      throw new BadRequestException("Session cannot be activated. Current status: " + session.getStatus());
    }

    // Ensure only one active session exists
    long activeSessionCount = sessionRepository.countByStatus(AcademicSession.SessionStatus.ACTIVE);
    if (activeSessionCount > 0) {
      throw new BadRequestException("Another session is already active. Please deactivate it first.");
    }

    session.setStatus(AcademicSession.SessionStatus.ACTIVE);
    sessionRepository.save(session);

    log.info("Academic session activated: {}", session.getSessionName());
  }

  @Transactional
  @Auditable(action = "DEACTIVATE_SESSION", entityType = "AcademicSession")
  public void deactivateSession(Long id) {
    AcademicSession session = findSessionById(id);

    if (session.getStatus() != AcademicSession.SessionStatus.ACTIVE) {
      throw new BadRequestException("Only active sessions can be deactivated");
    }

    session.setStatus(AcademicSession.SessionStatus.INACTIVE);
    sessionRepository.save(session);

    log.info("Academic session deactivated: {}", session.getSessionName());
  }

  @Transactional
  @Auditable(action = "ARCHIVE_SESSION", entityType = "AcademicSession")
  public void archiveSession(Long id) {
    AcademicSession session = findSessionById(id);

    if (!session.canBeArchived()) {
      throw new BadRequestException("Session cannot be archived. Current status: " + session.getStatus());
    }

    session.setStatus(AcademicSession.SessionStatus.ARCHIVED);
    sessionRepository.save(session);

    log.info("Academic session archived: {}", session.getSessionName());
  }

  @Transactional
  @Auditable(action = "DELETE_ACADEMIC_SESSION", entityType = "AcademicSession")
  public void deleteSession(Long id) {
    AcademicSession session = findSessionById(id);

    if (session.getStatus() == AcademicSession.SessionStatus.ACTIVE) {
      throw new BadRequestException("Cannot delete active session");
    }

    // Check if session has any classes
    if (session.getClasses() != null && !session.getClasses().isEmpty()) {
      throw new BadRequestException("Cannot delete session that has classes associated with it");
    }

    sessionRepository.delete(session);
    log.info("Academic session deleted successfully: {}", session.getSessionName());
  }

  private AcademicSession findSessionById(Long id) {
    return sessionRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Academic session", "id", id));
  }

  private void validateSessionRequest(AcademicSessionRequest request) {
    if (request.getStartDate().isAfter(request.getEndDate())) {
      throw new BadRequestException("Start date cannot be after end date");
    }

    if (request.getEndDate().isBefore(LocalDate.now().minusMonths(1))) {
      throw new BadRequestException("End date cannot be more than 1 month in the past");
    }
  }
}
