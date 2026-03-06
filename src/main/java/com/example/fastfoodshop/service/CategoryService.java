package com.example.fastfoodshop.service;

import com.example.fastfoodshop.dto.CategoryDTO;
import com.example.fastfoodshop.dto.ProductDTO;
import com.example.fastfoodshop.entity.Category;
import com.example.fastfoodshop.response.CategoryResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

public interface CategoryService {
    Category findCategoryOrThrow(Long id);

    Category findCategoryOrThrow(String slug);

    Category findUndeletedCategoryOrThrow(String categorySlug);

    Category findActivatedCategoryOrThrow(Long id);

    Category findDeactivatedCategoryOrThrow(Long id);

    void handleCategoryImage(Category category, MultipartFile imageFile);

    void applyPromotion(ProductDTO productDTO, Category category);

    ResponseEntity<ResponseWrapper<CategoryDTO>> createCategory(String name, String description, boolean activated, MultipartFile imageFile);

    ResponseEntity<ResponseWrapper<CategoryDTO>> updateCategory(Long id, String name, String description, boolean activated, MultipartFile imageFile);

    ResponseEntity<ResponseWrapper<CategoryResponse>> getCategories(int page, int size);

    ResponseEntity<ResponseWrapper<String>> activateCategory(Long id);

    ResponseEntity<ResponseWrapper<String>> deactivateCategory(Long id);

    ResponseEntity<ResponseWrapper<CategoryDTO>> deleteCategory(Long id);

    ResponseEntity<ResponseWrapper<ArrayList<CategoryDTO>>> getDisplayableCategories();
}
