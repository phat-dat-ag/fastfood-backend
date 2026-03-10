package com.example.fastfoodshop.exception.review;

import com.example.fastfoodshop.exception.base.BusinessException;

public class ReviewExpiredException extends BusinessException {
    public ReviewExpiredException(Long orderId) {
        super("REVIEW_EXPIRED", "Đơn hàng quá hạn đánh giá: " + orderId);
    }
}
