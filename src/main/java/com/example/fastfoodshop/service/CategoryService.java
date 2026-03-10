package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.CategoryDTO;
import com.example.fastfoodshop.dto.PromotionResult;
import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.request.CategoryCreateRequest;
import com.example.fastfoodshop.request.CategoryUpdateRequest;
import com.example.fastfoodshop.response.CategoryResponse;

import java.util.ArrayList;

public interface CategoryService {
    Category findCategoryOrThrow(Long categoryId);

    Category findCategoryOrThrow(String categorySlug);

    Category findUndeletedCategoryOrThrow(String categorySlug);

    Category findActivatedCategoryOrThrow(Long categoryId);

    Category findDeactivatedCategoryOrThrow(Long categoryId);

    PromotionResult applyPromotion(Product product, Category category);

    CategoryDTO createCategory(CategoryCreateRequest categoryCreateRequest);

    CategoryDTO updateCategory(CategoryUpdateRequest categoryUpdateRequest);

    CategoryResponse getCategories(int page, int size);

    String activateCategory(Long id);

    String deactivateCategory(Long id);

    CategoryDTO deleteCategory(Long id);

    ArrayList<CategoryDTO> getDisplayableCategories();
}
