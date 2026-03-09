package com.example.fastfoodshop.exception.category;

import com.example.fastfoodshop.exception.base.BusinessException;

public class CategoryNotFoundException extends BusinessException {
    public CategoryNotFoundException(String categorySlug) {
        super("CATEGORY_NOT_FOUND", "Danh mục không tồn tại: " + categorySlug);
    }

    public CategoryNotFoundException(Long categoryId) {
        super("CATEGORY_NOT_FOUND", "Danh mục không tồn tại: " + categoryId);
    }
}
