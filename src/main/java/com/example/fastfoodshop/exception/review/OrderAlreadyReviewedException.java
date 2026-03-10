package com.example.fastfoodshop.exception.review;

import com.example.fastfoodshop.exception.base.BusinessException;

public class OrderAlreadyReviewedException extends BusinessException {
    public OrderAlreadyReviewedException(Long orderId) {
        super("ORDER_ALREADY_REVIEWED", "Đơn hàng đã được đánh giá: " + orderId);
    }
}
