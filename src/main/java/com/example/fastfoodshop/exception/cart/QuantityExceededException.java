package com.example.fastfoodshop.exception.cart;

import com.example.fastfoodshop.exception.base.BusinessException;

public class QuantityExceededException extends BusinessException {
    public QuantityExceededException() {
        super("QUANTITY_EXCEEDED", "Số lượng sản phẩm vượt quá giới hạn");
    }
}
