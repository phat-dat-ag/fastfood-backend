package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.CategoryCreateRequest;
import com.example.fastfoodshop.request.CategoryUpdateRequest;
import com.example.fastfoodshop.request.PageRequest;
import com.example.fastfoodshop.request.UpdateActivationRequest;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.category.CategoryUpdateResponse;
import com.example.fastfoodshop.response.category.CategoryStatsResponse;
import com.example.fastfoodshop.response.category.CategoryPageResponse;
import com.example.fastfoodshop.response.category.CategoryResponse;
import com.example.fastfoodshop.response.category.CategorySelectionResponse;
import com.example.fastfoodshop.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController extends BaseController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<CategoryResponse>> createCategory(
            @Valid @ModelAttribute CategoryCreateRequest categoryCreateRequest
    ) {
        return okResponse(categoryService.createCategory(categoryCreateRequest));
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<CategoryPageResponse>> getCategoryPage(
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(categoryService.getCategoryPage(request.getPage(), request.getSize()));
    }

    @GetMapping("/selection")
    public ResponseEntity<ResponseWrapper<CategorySelectionResponse>> getCategorySelections() {
        return okResponse(categoryService.getCategorySelections());
    }

    @PutMapping("{id}")
    public ResponseEntity<ResponseWrapper<CategoryResponse>> updateCategory(
            @PathVariable("id") Long categoryId,
            @Valid @ModelAttribute CategoryUpdateRequest categoryUpdateRequest
    ) {
        return okResponse(categoryService.updateCategory(categoryId, categoryUpdateRequest));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseWrapper<CategoryUpdateResponse>> updateCategoryActivation(
            @PathVariable("id") Long categoryId,
            @Valid @RequestBody UpdateActivationRequest updateActivationRequest
    ) {
        return okResponse(categoryService.updateCategoryActivation(categoryId, updateActivationRequest.activated()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<CategoryUpdateResponse>> deleteCategory(
            @PathVariable("id") Long categoryId
    ) {
        return okResponse(categoryService.deleteCategory(categoryId));
    }

    @GetMapping("/stats")
    public ResponseEntity<ResponseWrapper<CategoryStatsResponse>> getCategoryStats() {
        return okResponse(categoryService.getCategoryStats());
    }
}
