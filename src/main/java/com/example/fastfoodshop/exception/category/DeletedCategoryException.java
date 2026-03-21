package com.example.fastfoodshop.exception.category;

import com.example.fastfoodshop.exception.base.BusinessException;

public class DeletedCategoryException extends BusinessException {
    public DeletedCategoryException(Long categoryId) {
        super("DELETED_CATEGORY", "Danh mục sản phẩm đã bị xóa: " + categoryId);
    }

    public DeletedCategoryException(String categorySlug) {
        super("DELETED_CATEGORY", "Danh mục sản phẩm đã bị xóa: " + categorySlug);
    }
}
