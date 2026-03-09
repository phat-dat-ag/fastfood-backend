package com.example.fastfoodshop.exception.cart;

import com.example.fastfoodshop.exception.base.BusinessException;

public class CartNotFoundException extends BusinessException {
    public CartNotFoundException(Long productId) {
        super("CART_NOT_FOUND", "Sản phẩm không tồn tại trong giỏ hàng: " + productId);
    }
}
