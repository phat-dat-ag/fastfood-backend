package com.example.fastfoodshop.exception.quiz;

import com.example.fastfoodshop.exception.base.BusinessException;

public class NotAllowFeedbackException extends BusinessException {
    public NotAllowFeedbackException() {
        super("NOT_ALLOW_FEEDBACK", "Bài kiểm tra đã được đánh giá hoặc quá hạn đánh giá");
    }
}
