package com.saqib.school.fee.service;

import com.saqib.school.common.audit.Auditable;
import com.saqib.school.common.dto.PageResponse;
import com.saqib.school.common.exception.BadRequestException;
import com.saqib.school.common.exception.ResourceNotFoundException;
import com.saqib.school.fee.entity.FeeCategory;
import com.saqib.school.fee.mapper.FeeCategoryMapper;
import com.saqib.school.fee.model.FeeCategoryRequest;
import com.saqib.school.fee.model.FeeCategoryResponse;
import com.saqib.school.fee.repository.FeeCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeeCategoryService {

    private final FeeCategoryRepository feeCategoryRepository;
    private final FeeCategoryMapper feeCategoryMapper;

    @Transactional
    @Auditable(action = "CREATE_FEE_CATEGORY", entityType = "FeeCategory")
    public FeeCategoryResponse createFeeCategory(FeeCategoryRequest request) {
        validateCategoryNameUniqueness(request.getCategoryName(), null);

        var feeCategory = feeCategoryMapper.toEntity(request);
        feeCategory.setIsActive(true);

        feeCategory = feeCategoryRepository.save(feeCategory);
        log.info("Fee category created successfully: {}", feeCategory.getCategoryName());

        return feeCategoryMapper.toResponse(feeCategory);
    }

    @Transactional(readOnly = true)
    public FeeCategoryResponse getFeeCategoryById(Long id) {
        var feeCategory = findFeeCategoryById(id);
        return feeCategoryMapper.toResponse(feeCategory);
    }

    @Transactional(readOnly = true)
    public FeeCategoryResponse getFeeCategoryByName(String categoryName) {
        var feeCategory = feeCategoryRepository.findByCategoryName(categoryName)
                .orElseThrow(() -> new ResourceNotFoundException("Fee Category", "categoryName", categoryName));
        return feeCategoryMapper.toResponse(feeCategory);
    }

    @Transactional(readOnly = true)
    public PageResponse<FeeCategoryResponse> getAllFeeCategories(Pageable pageable) {
        var page = feeCategoryRepository.findAll(pageable)
                .map(feeCategoryMapper::toResponse);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public PageResponse<FeeCategoryResponse> getActiveFeeCategories(Pageable pageable) {
        var page = feeCategoryRepository.findByIsActive(true, pageable)
                .map(feeCategoryMapper::toResponse);
        return PageResponse.from(page);
    }


    @Transactional(readOnly = true)
    public PageResponse<FeeCategoryResponse> searchFeeCategories(String searchTerm, Pageable pageable) {
        var page = feeCategoryRepository.findBySearchTerm(searchTerm, pageable)
                .map(feeCategoryMapper::toResponse);
        return PageResponse.from(page);
    }

    @Transactional
    @Auditable(action = "UPDATE_FEE_CATEGORY", entityType = "FeeCategory")
    public FeeCategoryResponse updateFeeCategory(Long id, FeeCategoryRequest request) {
        var feeCategory = findFeeCategoryById(id);

        if (request.getCategoryName() != null) {
            validateCategoryNameUniqueness(request.getCategoryName(), id);
        }

        feeCategoryMapper.updateEntity(request, feeCategory);
        feeCategory = feeCategoryRepository.save(feeCategory);

        log.info("Fee category updated successfully: {}", feeCategory.getCategoryName());
        return feeCategoryMapper.toResponse(feeCategory);
    }

    @Transactional
    @Auditable(action = "TOGGLE_FEE_CATEGORY_STATUS", entityType = "FeeCategory")
    public void toggleFeeCategoryStatus(Long id) {
        var feeCategory = findFeeCategoryById(id);
        feeCategory.setIsActive(!feeCategory.getIsActive());
        feeCategoryRepository.save(feeCategory);

        log.info("Fee category status toggled to {} for: {}",
                feeCategory.getIsActive(), feeCategory.getCategoryName());
    }

    @Transactional
    @Auditable(action = "DELETE_FEE_CATEGORY", entityType = "FeeCategory")
    public void deleteFeeCategory(Long id) {
        FeeCategory feeCategory = findFeeCategoryById(id);

        if (!feeCategory.canBeDeleted()) {
            throw new BadRequestException("Fee category cannot be deleted as it has associated fee structures");
        }

        feeCategoryRepository.delete(feeCategory);
        log.info("Fee category deleted successfully: {}", feeCategory.getCategoryName());
    }

    private FeeCategory findFeeCategoryById(Long id) {
        return feeCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fee Category", "id", id));
    }

    private void validateCategoryNameUniqueness(String categoryName, Long excludeId) {
        boolean nameExists = excludeId != null ?
                feeCategoryRepository.findByCategoryName(categoryName)
                        .map(FeeCategory::getId)
                        .filter(id -> !id.equals(excludeId))
                        .isPresent() :
                feeCategoryRepository.existsByCategoryName(categoryName);

        if (nameExists) {
            throw new BadRequestException("Fee category name already exists: " + categoryName);
        }
    }
}
