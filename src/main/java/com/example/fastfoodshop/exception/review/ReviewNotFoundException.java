package com.example.fastfoodshop.exception.review;

import com.example.fastfoodshop.exception.base.BusinessException;

public class ReviewNotFoundException extends BusinessException {
    public ReviewNotFoundException(Long reviewId) {
        super("REVIEW_NOT_FOUND", "Đánh giá không tồn tại: " + reviewId);
    }
}
