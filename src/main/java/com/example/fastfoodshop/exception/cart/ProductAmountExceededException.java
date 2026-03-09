package com.example.fastfoodshop.exception.cart;

import com.example.fastfoodshop.exception.base.BusinessException;

public class ProductAmountExceededException extends BusinessException {
    public ProductAmountExceededException() {
        super("PRODUCT_AMOUNT_EXCEEDED", "Số loại sản phẩm vượt quá giới hạn");
    }
}
