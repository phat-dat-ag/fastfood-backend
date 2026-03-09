package com.example.fastfoodshop.exception.order;

import com.example.fastfoodshop.exception.base.BusinessException;

public class OrderNotFoundException extends BusinessException {
    public OrderNotFoundException(Long orderId) {
        super("ORDER_NOT_FOUND", "Đơn hàng không tồn tại: " + orderId);
    }
}
