package org.assignments.category.service;

import org.assignments.category.dto.request.CreateCategoryRequest;
import org.assignments.category.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CreateCategoryRequest request);

    CategoryResponse getCategoryById(Long id);

    List<CategoryResponse> getAllCategories();

    List<CategoryResponse> getRootCategories();

    void softDeleteCategory(Long id);
}
