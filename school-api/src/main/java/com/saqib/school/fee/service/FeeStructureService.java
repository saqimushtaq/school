package com.saqib.school.fee.service;

import com.saqib.school.academic.entity.SchoolClass;
import com.saqib.school.academic.repository.SchoolClassRepository;
import com.saqib.school.common.audit.Auditable;
import com.saqib.school.common.dto.PageResponse;
import com.saqib.school.common.exception.BadRequestException;
import com.saqib.school.common.exception.ResourceNotFoundException;
import com.saqib.school.fee.entity.FeeCategory;
import com.saqib.school.fee.entity.FeeStructure;
import com.saqib.school.fee.mapper.FeeStructureMapper;
import com.saqib.school.fee.model.*;
import com.saqib.school.fee.repository.FeeCategoryRepository;
import com.saqib.school.fee.repository.FeeStructureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeeStructureService {

    private final FeeStructureRepository feeStructureRepository;
    private final FeeCategoryRepository feeCategoryRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final FeeStructureMapper feeStructureMapper;

    @Transactional
    @Auditable(action = "CREATE_FEE_STRUCTURE", entityType = "FeeStructure")
    public FeeStructureResponse createFeeStructure(FeeStructureRequest request) {
        validateFeeStructureUniqueness(request.getClassId(), request.getFeeCategoryId(), null);

        SchoolClass schoolClass = findSchoolClassById(request.getClassId());
        FeeCategory feeCategory = findFeeCategoryById(request.getFeeCategoryId());

        FeeStructure feeStructure = feeStructureMapper.toEntity(request);
        feeStructure.setSchoolClass(schoolClass);
        feeStructure.setFeeCategory(feeCategory);
        feeStructure.setIsActive(true);

        FeeStructure savedStructure = feeStructureRepository.save(feeStructure);
        log.info("Fee structure created for class {} and category {}",
                 schoolClass.getDisplayName(), feeCategory.getCategoryName());

        return feeStructureMapper.toResponse(savedStructure);
    }

    @Transactional(readOnly = true)
    public FeeStructureResponse getFeeStructureById(Long id) {
        FeeStructure feeStructure = findFeeStructureById(id);
        return feeStructureMapper.toResponse(feeStructure);
    }

    @Transactional(readOnly = true)
    public List<FeeStructureResponse> getFeeStructuresByClass(Long classId) {
        return feeStructureRepository.findByClassIdAndActive(classId)
            .stream()
            .map(feeStructureMapper::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<FeeStructureResponse> getFeeStructuresByCategory(Long categoryId) {
        return feeStructureRepository.findByFeeCategoryIdAndActive(categoryId)
            .stream()
            .map(feeStructureMapper::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<FeeStructureResponse> getFeeStructuresBySession(Long sessionId, Pageable pageable) {
        var page = feeStructureRepository.findBySessionIdAndActive(sessionId, pageable)
            .map(feeStructureMapper::toResponse);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public List<FeeStructureResponse> getAllActiveFeeStructures() {
        return feeStructureRepository.findAllActiveWithDetails()
            .stream()
            .map(feeStructureMapper::toResponse)
            .toList();
    }

    @Transactional
    @Auditable(action = "UPDATE_FEE_STRUCTURE", entityType = "FeeStructure")
    public FeeStructureResponse updateFeeStructure(Long id, FeeStructureUpdateRequest request) {
        FeeStructure feeStructure = findFeeStructureById(id);
        feeStructureMapper.updateEntity(request, feeStructure);

        FeeStructure updatedStructure = feeStructureRepository.save(feeStructure);
        log.info("Fee structure updated for class {} and category {}",
                 feeStructure.getSchoolClass().getDisplayName(),
                 feeStructure.getFeeCategory().getCategoryName());

        return feeStructureMapper.toResponse(updatedStructure);
    }

    @Transactional
    @Auditable(action = "BULK_UPDATE_FEE_STRUCTURES", entityType = "FeeStructure")
    public List<FeeStructureResponse> bulkUpdateFeeStructures(BulkFeeUpdateRequest request) {
        List<FeeStructure> updatedStructures = new ArrayList<>();

        for (BulkFeeUpdateRequest.FeeUpdateItem updateItem : request.getFeeUpdates()) {
            SchoolClass schoolClass = findSchoolClassById(updateItem.getClassId());
            FeeCategory feeCategory = findFeeCategoryById(updateItem.getFeeCategoryId());

            FeeStructure feeStructure = feeStructureRepository
                .findByClassIdAndCategoryId(updateItem.getClassId(), updateItem.getFeeCategoryId())
                .orElseGet(() -> {
                    FeeStructure newStructure = new FeeStructure();
                    newStructure.setSchoolClass(schoolClass);
                    newStructure.setFeeCategory(feeCategory);
                    newStructure.setIsMonthly(true);
                    newStructure.setIsActive(true);
                    return newStructure;
                });

            feeStructure.setAmount(updateItem.getAmount());
            updatedStructures.add(feeStructureRepository.save(feeStructure));
        }

        log.info("Bulk fee structure update completed for {} items", updatedStructures.size());
        return updatedStructures.stream()
            .map(feeStructureMapper::toResponse)
            .toList();
    }

    @Transactional
    @Auditable(action = "TOGGLE_FEE_STRUCTURE_STATUS", entityType = "FeeStructure")
    public void toggleFeeStructureStatus(Long id) {
        FeeStructure feeStructure = findFeeStructureById(id);
        feeStructure.setIsActive(!feeStructure.getIsActive());
        feeStructureRepository.save(feeStructure);

        log.info("Fee structure status toggled to {} for class {} and category {}",
                 feeStructure.getIsActive(),
                 feeStructure.getSchoolClass().getDisplayName(),
                 feeStructure.getFeeCategory().getCategoryName());
    }

    @Transactional
    @Auditable(action = "DELETE_FEE_STRUCTURE", entityType = "FeeStructure")
    public void deleteFeeStructure(Long id) {
        FeeStructure feeStructure = findFeeStructureById(id);
        feeStructureRepository.delete(feeStructure);

        log.info("Fee structure deleted for class {} and category {}",
                 feeStructure.getSchoolClass().getDisplayName(),
                 feeStructure.getFeeCategory().getCategoryName());
    }

    private FeeStructure findFeeStructureById(Long id) {
        return feeStructureRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Fee Structure", "id", id));
    }

    private SchoolClass findSchoolClassById(Long id) {
        return schoolClassRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("School Class", "id", id));
    }

    private FeeCategory findFeeCategoryById(Long id) {
        return feeCategoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Fee Category", "id", id));
    }

    private void validateFeeStructureUniqueness(Long classId, Long categoryId, Long excludeId) {
        boolean exists = excludeId != null ?
            feeStructureRepository.findByClassIdAndCategoryId(classId, categoryId)
                .map(FeeStructure::getId)
                .filter(id -> !id.equals(excludeId))
                .isPresent() :
            feeStructureRepository.existsBySchoolClassIdAndFeeCategoryId(classId, categoryId);

        if (exists) {
            throw new BadRequestException("Fee structure already exists for this class and category combination");
        }
    }
}
