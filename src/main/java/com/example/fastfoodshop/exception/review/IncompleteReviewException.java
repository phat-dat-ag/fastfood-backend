package com.example.fastfoodshop.exception.review;

import com.example.fastfoodshop.exception.base.BusinessException;

public class IncompleteReviewException extends BusinessException {
    public IncompleteReviewException() {
        super("INCOMPLETE_REVIEW", "Lỗi có sản phẩm chưa được đánh giá");
    }
}
