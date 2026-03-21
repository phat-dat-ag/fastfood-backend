package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.request.CategoryCreateRequest;
import com.example.fastfoodshop.request.CategoryUpdateRequest;
import com.example.fastfoodshop.request.PageRequest;
import com.example.fastfoodshop.request.UpdateActivationRequest;
import com.example.fastfoodshop.response.category.CategoryDisplayResponse;
import com.example.fastfoodshop.response.category.CategoryPageResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.response.category.CategoryResponse;
import com.example.fastfoodshop.response.category.CategoryUpdateResponse;
import com.example.fastfoodshop.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/category")
@RequiredArgsConstructor
public class CategoryController extends BaseController {
    private final CategoryService categoryService;

    @PostMapping()
    public ResponseEntity<ResponseWrapper<CategoryResponse>> createCategory(
            @ModelAttribute CategoryCreateRequest categoryCreateRequest
    ) {
        return okResponse(categoryService.createCategory(categoryCreateRequest));
    }

    @GetMapping()
    public ResponseEntity<ResponseWrapper<CategoryPageResponse>> getCategories(
            @Valid @ModelAttribute PageRequest request
    ) {
        return okResponse(categoryService.getCategories(request.getPage(), request.getSize()));
    }

    @GetMapping("/display")
    public ResponseEntity<ResponseWrapper<CategoryDisplayResponse>> getDisplayableCategories() {
        return okResponse(categoryService.getDisplayableCategories());
    }

    @PutMapping()
    public ResponseEntity<ResponseWrapper<CategoryResponse>> updateCategory(
            @ModelAttribute CategoryUpdateRequest categoryUpdateRequest
    ) {
        return okResponse(categoryService.updateCategory(categoryUpdateRequest));
    }

    @PatchMapping("/{id}/activation")
    public ResponseEntity<ResponseWrapper<CategoryUpdateResponse>> updateCategoryActivation(
            @PathVariable("id") Long categoryId,
            @RequestBody UpdateActivationRequest updateActivationRequest
    ) {
        return okResponse(categoryService.updateCategoryActivation(categoryId, updateActivationRequest.activated()));
    }

    @DeleteMapping()
    public ResponseEntity<ResponseWrapper<CategoryUpdateResponse>> deleteCategory(
            @RequestParam("id") Long categoryId
    ) {
        return okResponse(categoryService.deleteCategory(categoryId));
    }
}
