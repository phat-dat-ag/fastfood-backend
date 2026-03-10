package com.example.fastfoodshop.exception.review;

import com.example.fastfoodshop.exception.base.BusinessException;

public class DuplicateReviewProductException extends BusinessException {
    public DuplicateReviewProductException() {
        super("DUPLICATE_REVIEW_PRODUCT", "Lỗi sản phẩm đã được đánh giá");
    }
}
