package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.PromotionResult;
import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.entity.Product;
import com.example.fastfoodshop.request.CategoryCreateRequest;
import com.example.fastfoodshop.request.CategoryUpdateRequest;
import com.example.fastfoodshop.response.category.CategoryDisplayResponse;
import com.example.fastfoodshop.response.category.CategoryPageResponse;
import com.example.fastfoodshop.response.category.CategoryResponse;
import com.example.fastfoodshop.response.category.CategoryUpdateResponse;
import com.example.fastfoodshop.response.category.CategorySelectionResponse;

public interface CategoryService {
    Category findCategoryOrThrow(Long categoryId);

    Category findCategoryOrThrow(String categorySlug);

    PromotionResult applyPromotion(Product product, Category category);

    CategoryResponse createCategory(CategoryCreateRequest categoryCreateRequest);

    CategorySelectionResponse getCategorySelections();

    CategoryResponse updateCategory(Long categoryId, CategoryUpdateRequest categoryUpdateRequest);

    CategoryPageResponse getCategoryPage(int page, int size);

    CategoryUpdateResponse updateCategoryActivation(Long categoryId, boolean activated);

    CategoryUpdateResponse deleteCategory(Long id);

    CategoryDisplayResponse getAllDisplayableCategories();
}
