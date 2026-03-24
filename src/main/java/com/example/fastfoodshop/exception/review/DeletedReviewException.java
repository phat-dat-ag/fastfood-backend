package com.example.fastfoodshop.exception.review;

import com.example.fastfoodshop.exception.base.BusinessException;

public class DeletedReviewException extends BusinessException {
    public DeletedReviewException() {
        super("DELETED_REVIEW", "Đánh giá đã bị xóa");
    }
}
