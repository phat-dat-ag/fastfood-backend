package com.example.fastfoodshop.exception.category;

import com.example.fastfoodshop.exception.base.BusinessException;

public class UnavailableCategoryException extends BusinessException {
    public UnavailableCategoryException(String categoryName) {
        super("UNAVAILABLE_CATEGORY", "Danh mục đã bị xóa hoặc ngừng kinh doanh: " + categoryName);
    }
}
