package com.example.fastfoodshop.exception.order;

import com.example.fastfoodshop.exception.base.BusinessException;

public class CartEmptyException extends BusinessException {
    public CartEmptyException() {
        super("CART_EMPTY", "Không tìm thấy sản phẩm để tạo đơn hàng");
    }
}