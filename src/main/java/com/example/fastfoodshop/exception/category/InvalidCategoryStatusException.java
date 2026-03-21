package com.example.fastfoodshop.exception.category;

import com.example.fastfoodshop.exception.base.BusinessException;

public class InvalidCategoryStatusException extends BusinessException {
    public InvalidCategoryStatusException() {
        super("INVALID_CATEGORY_STATUS", "Trạng thái của danh mục sản phẩm không hợp lệ");
    }
}
