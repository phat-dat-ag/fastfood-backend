package com.example.fastfoodshop.exception.quiz;

import com.example.fastfoodshop.exception.base.BusinessException;

public class QuizAlreadyFeedbackException extends BusinessException {
    public QuizAlreadyFeedbackException() {
        super("QUIZ_ALREADY_FEEDBACK", "Bài kiểm tra đã được đánh giá");
    }
}
