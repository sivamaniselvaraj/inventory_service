package org.assignments.category.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assignments.category.dto.request.CreateCategoryRequest;
import org.assignments.category.dto.response.CategoryResponse;
import org.assignments.category.entity.Category;
import org.assignments.inventory.exception.DuplicateResourceException;
import org.assignments.inventory.exception.ResourceNotFoundException;
import org.assignments.category.repository.CategoryRepository;
import org.assignments.category.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        log.info("Creating category with code: {}", request.getCode());

        if (categoryRepository.existsByCodeIgnoreCase(request.getCode())) {
            throw new DuplicateResourceException("Category", "code", request.getCode());
        }

        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findActiveById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getParentId()));
        }

        Category category = Category.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .sortOrder(request.getSortOrder())
                .parent(parent)
                .build();


        Category saved = categoryRepository.save(category);
        log.info("Category created with id: {}", saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAllActive().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getRootCategories() {
        return categoryRepository.findAllRootCategories().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void softDeleteCategory(Long id) {
        log.info("Soft deleting category with id: {}", id);
        Category category = categoryRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        category.setDeleted(true);
        category.setActive(false);
        categoryRepository.save(category);
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .code(category.getCode())
                .name(category.getName())
                .description(category.getDescription())
                .sortOrder(category.getSortOrder())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .active(category.isActive())
                .createdAt(category.getCreatedAt())
                .build();
    }
}
