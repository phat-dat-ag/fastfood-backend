package com.example.fastfoodshop.exception.order;

import com.example.fastfoodshop.exception.base.BusinessException;

public class OrderAmountExceededException extends BusinessException {
    public OrderAmountExceededException() {
        super("ORDER_AMOUNT_EXCEEDED", "Tổng giá trị đơn hàng vượt quá giới hạn cho phép");
    }
}
