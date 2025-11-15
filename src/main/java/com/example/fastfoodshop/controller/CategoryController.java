package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.dto.CategoryDTO;
import com.example.fastfoodshop.request.CategoryCreateRequest;
import com.example.fastfoodshop.request.CategoryUpdateRequest;
import com.example.fastfoodshop.request.PageRequest;
import com.example.fastfoodshop.response.CategoryResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@RequestMapping("/api/admin/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping()
    public ResponseEntity<ResponseWrapper<CategoryDTO>> createCategory(@ModelAttribute CategoryCreateRequest req) {
        return categoryService.createCategory(req.getName(), req.getDescription(), req.isActivated(), req.getImageUrl());
    }

    @GetMapping()
    public ResponseEntity<ResponseWrapper<CategoryResponse>> getCategories(
            @Valid @ModelAttribute PageRequest request
    ) {
        return categoryService.getCategories(request.getPage(), request.getSize());
    }

    @GetMapping("/display")
    public ResponseEntity<ResponseWrapper<ArrayList<CategoryDTO>>> getDisplayableCategories() {
        return categoryService.getDisplayableCategories();
    }

    @PutMapping()
    public ResponseEntity<ResponseWrapper<CategoryDTO>> updateCategory(@ModelAttribute CategoryUpdateRequest req) {
        return categoryService.updateCategory(req.getId(), req.getName(), req.getDescription(), req.isActivated(), req.getImageUrl());
    }

    @PutMapping("/activate")
    public ResponseEntity<ResponseWrapper<String>> activateCategory(@RequestParam("id") Long id) {
        return categoryService.activateCategory(id);
    }

    @PutMapping("/deactivate")
    public ResponseEntity<ResponseWrapper<String>> deactivateCategory(@RequestParam("id") Long id) {
        return categoryService.deactivateCategory(id);
    }

    @DeleteMapping()
    public ResponseEntity<ResponseWrapper<CategoryDTO>> deleteCategory(@RequestParam("id") Long id) {
        return categoryService.deleteCategory(id);
    }
}
