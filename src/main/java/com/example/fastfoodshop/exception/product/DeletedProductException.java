package com.example.fastfoodshop.exception.product;

import com.example.fastfoodshop.exception.base.BusinessException;

public class DeletedProductException extends BusinessException {
    public DeletedProductException(Long productId) {
        super("DELETED_PRODUCT", "Sản phẩm đã bị xóa: " + productId);
    }
}
