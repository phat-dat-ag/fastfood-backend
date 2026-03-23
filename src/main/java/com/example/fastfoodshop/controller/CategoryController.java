package com.example.fastfoodshop.controller;

import com.example.fastfoodshop.response.category.CategoryDisplayResponse;
import com.example.fastfoodshop.response.ResponseWrapper;
import com.example.fastfoodshop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController extends BaseController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<CategoryDisplayResponse>> getAllDisplayableCategories() {
        return okResponse(categoryService.getAllDisplayableCategories());
    }
}
