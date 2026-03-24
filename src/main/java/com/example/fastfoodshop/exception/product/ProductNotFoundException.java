package com.example.fastfoodshop.exception.product;

import com.example.fastfoodshop.exception.base.BusinessException;

public class ProductNotFoundException extends BusinessException {
    public ProductNotFoundException() {
        super("PRODUCT_NOT_FOUND", "Sản phẩm không tồn tại");
    }

    public ProductNotFoundException(Long productId) {
        super("PRODUCT_NOT_FOUND", "Sản phẩm không tồn tại: " + productId);
    }

    public ProductNotFoundException(String productSlug) {
        super("PRODUCT_NOT_FOUND", "Sản phẩm không tồn tại: " + productSlug);
    }
}
