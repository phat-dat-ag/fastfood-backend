package com.example.fastfoodshop.exception.product;

import com.example.fastfoodshop.exception.base.BusinessException;

public class InvalidStatusProductException extends BusinessException {
    public InvalidStatusProductException(Long productId) {
        super("INVALID_PRODUCT", "Trạng thái sản phẩm không hợp lệ: " + productId);
    }
}
