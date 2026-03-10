package com.example.fastfoodshop.exception.review;

import com.example.fastfoodshop.exception.base.BusinessException;

public class OrderNotDeliveredException extends BusinessException {
    public OrderNotDeliveredException(Long orderId) {
        super("ORDER_NOT_DELIVERED", "Không đánh giá cho đơn hàng chưa được giao thành công: " + orderId);
    }
}
