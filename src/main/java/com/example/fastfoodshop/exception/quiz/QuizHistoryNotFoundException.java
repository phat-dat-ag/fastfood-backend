package com.example.fastfoodshop.exception.quiz;

import com.example.fastfoodshop.exception.base.BusinessException;

public class QuizHistoryNotFoundException extends BusinessException {
    public QuizHistoryNotFoundException(Long quizId) {
        super("QUIZ_HISTORY_NOT_FOUND", "Lịch sử tham gia không tồn tại: " + quizId);
    }
}
