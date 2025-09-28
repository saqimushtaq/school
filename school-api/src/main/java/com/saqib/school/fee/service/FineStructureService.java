package com.saqib.school.fee.service;

import com.saqib.school.academic.entity.SchoolClass;
import com.saqib.school.academic.repository.SchoolClassRepository;
import com.saqib.school.common.audit.Auditable;
import com.saqib.school.common.dto.PageResponse;
import com.saqib.school.common.exception.BadRequestException;
import com.saqib.school.common.exception.ResourceNotFoundException;
import com.saqib.school.fee.entity.FineStructure;
import com.saqib.school.fee.mapper.FineStructureMapper;
import com.saqib.school.fee.model.FineStructureRequest;
import com.saqib.school.fee.model.FineStructureResponse;
import com.saqib.school.fee.repository.FineStructureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FineStructureService {

    private final FineStructureRepository fineStructureRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final FineStructureMapper fineStructureMapper;

    @Transactional
    @Auditable(action = "CREATE_FINE_STRUCTURE", entityType = "FineStructure")
    public FineStructureResponse createFineStructure(FineStructureRequest request) {
        validateFineStructureUniqueness(request.getClassId(), request.getDaysAfterDue(), null);

        SchoolClass schoolClass = findSchoolClassById(request.getClassId());

        FineStructure fineStructure = fineStructureMapper.toEntity(request);
        fineStructure.setSchoolClass(schoolClass);
        fineStructure.setIsActive(true);

        FineStructure savedStructure = fineStructureRepository.save(fineStructure);
        log.info("Fine structure created for class {} - {} days after due",
                 schoolClass.getDisplayName(), request.getDaysAfterDue());

        return fineStructureMapper.toResponse(savedStructure);
    }

    @Transactional(readOnly = true)
    public FineStructureResponse getFineStructureById(Long id) {
        FineStructure fineStructure = findFineStructureById(id);
        return fineStructureMapper.toResponse(fineStructure);
    }

    @Transactional(readOnly = true)
    public List<FineStructureResponse> getFineStructuresByClass(Long classId) {
        return fineStructureRepository.findByClassIdAndActiveOrderByDays(classId)
            .stream()
            .map(fineStructureMapper::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<FineStructureResponse> getFineStructuresBySession(Long sessionId, Pageable pageable) {
        var page = fineStructureRepository.findBySessionIdAndActive(sessionId, pageable)
            .map(fineStructureMapper::toResponse);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public PageResponse<FineStructureResponse> getActiveFineStructures(Pageable pageable) {
        var page = fineStructureRepository.findByIsActive(true, pageable)
            .map(fineStructureMapper::toResponse);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public List<FineStructureResponse> getAllActiveFineStructures() {
        return fineStructureRepository.findAllActiveWithClass()
            .stream()
            .map(fineStructureMapper::toResponse)
            .toList();
    }

    @Transactional
    @Auditable(action = "UPDATE_FINE_STRUCTURE", entityType = "FineStructure")
    public FineStructureResponse updateFineStructure(Long id, FineStructureRequest request) {
        FineStructure fineStructure = findFineStructureById(id);

        if (!request.getDaysAfterDue().equals(fineStructure.getDaysAfterDue())) {
            validateFineStructureUniqueness(fineStructure.getSchoolClass().getId(), request.getDaysAfterDue(), id);
        }

        fineStructureMapper.updateEntity(request, fineStructure);
        FineStructure updatedStructure = fineStructureRepository.save(fineStructure);

        log.info("Fine structure updated for class {} - {} days after due",
                 fineStructure.getSchoolClass().getDisplayName(), request.getDaysAfterDue());

        return fineStructureMapper.toResponse(updatedStructure);
    }

    @Transactional
    @Auditable(action = "TOGGLE_FINE_STRUCTURE_STATUS", entityType = "FineStructure")
    public void toggleFineStructureStatus(Long id) {
        FineStructure fineStructure = findFineStructureById(id);
        fineStructure.setIsActive(!fineStructure.getIsActive());
        fineStructureRepository.save(fineStructure);

        log.info("Fine structure status toggled to {} for class {} - {} days after due",
                 fineStructure.getIsActive(),
                 fineStructure.getSchoolClass().getDisplayName(),
                 fineStructure.getDaysAfterDue());
    }

    @Transactional
    @Auditable(action = "DELETE_FINE_STRUCTURE", entityType = "FineStructure")
    public void deleteFineStructure(Long id) {
        FineStructure fineStructure = findFineStructureById(id);
        fineStructureRepository.delete(fineStructure);

        log.info("Fine structure deleted for class {} - {} days after due",
                 fineStructure.getSchoolClass().getDisplayName(),
                 fineStructure.getDaysAfterDue());
    }

    private FineStructure findFineStructureById(Long id) {
        return fineStructureRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Fine Structure", "id", id));
    }

    private SchoolClass findSchoolClassById(Long id) {
        return schoolClassRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("School Class", "id", id));
    }

    private void validateFineStructureUniqueness(Long classId, Integer daysAfterDue, Long excludeId) {
        boolean exists = excludeId != null ?
            fineStructureRepository.findByClassIdAndDaysAfterDue(classId, daysAfterDue)
                .map(FineStructure::getId)
                .filter(id -> !id.equals(excludeId))
                .isPresent() :
            fineStructureRepository.existsBySchoolClassIdAndDaysAfterDue(classId, daysAfterDue);

        if (exists) {
            throw new BadRequestException("Fine structure already exists for this class and days after due combination");
        }
    }
}
